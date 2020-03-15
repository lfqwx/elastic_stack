package com.cy.search;

import com.alibaba.fastjson.JSON;
import com.cy.config.ElasticConfig;
import com.cy.entity.Student;
import com.cy.mapper.StudentMapper;
import com.google.common.collect.Lists;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @author: 𝓛.𝓕.𝓠
 */
@Service
public class EsSearch {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private StudentMapper mapper;

    /**
     * 文档操作
     */
    public List query(String keyword,int from,int size) throws IOException {
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        boolQuery.should(QueryBuilders.matchQuery("name",keyword))
                .should(QueryBuilders.termQuery("gender",keyword))
                .should(QueryBuilders.matchQuery("minzu",keyword))
                .should(QueryBuilders.termQuery("university",keyword))
                .should(QueryBuilders.matchPhrasePrefixQuery("university",keyword))
                .should(QueryBuilders.termQuery("professional",keyword))
                .should(QueryBuilders.matchPhrasePrefixQuery("professional",keyword))
                .should(QueryBuilders.termQuery("addr",keyword))
                .should(QueryBuilders.matchPhrasePrefixQuery("addr",keyword))
                .should(QueryBuilders.termQuery("degree",keyword))
                .should(QueryBuilders.matchQuery("degree",keyword));
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQuery);
        sourceBuilder.from(from);
        sourceBuilder.size(size);
        System.out.println("sourceBuilder:"+sourceBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        List<Object> list = new ArrayList<>();
        for (SearchHit hit : hits.getHits()) {
            // do something with the SearchHit
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            list.add(sourceAsMap);
        }
        return list;
    }
    public void create(Student stu) throws IOException {
        //补全suggest字段
        if (!addSuggest(stu)) return;
        String stuJson = JSON.toJSONString(stu);
        IndexRequest request = new IndexRequest(ElasticConfig.INDEX).id(String.valueOf(stu.getId()))
                .source(stuJson, XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        if (response.getResult() == DocWriteResponse.Result.CREATED) {
            System.out.println("文档ID为:" + stu.getId() + "的信息写入索引成功！");
        } else {
            System.out.println("文档ID为:" + stu.getId() + "的信息写入索引失败！");
        }
    }

    public void update(Student stu,String field,String value) throws IOException {
        switch (field){
            case "id":
                stu.setId(value);
                break;
            case "name":
                stu.setName(value);
                break;
            case "gender":
                stu.setGender(value);
                break;
            case "minzu":
                stu.setMinzu(value);
                break;
            case "professional":
                stu.setProfessional(value);
                break;
            case "university":
                stu.setUniversity(value);
                break;
            case "degree":
                stu.setDegree(value);
                break;
            case "addr":
                stu.setAddr(value);
                break;
        }
        //更新数据库
        mapper.delete(stu.getId());
        mapper.insert(stu);
        //补全suggest字段
        if (!addSuggest(stu)) return;
        UpdateRequest request = new UpdateRequest(ElasticConfig.INDEX, stu.getId())
                .doc(field, value);
        UpdateResponse response = client.update(
                request, RequestOptions.DEFAULT);
        if (response.getResult() == DocWriteResponse.Result.UPDATED) {
            System.out.println("文档ID为:" + stu.getId() + "的信息更新索引成功！");
        } else {
            System.out.println("文档ID为:" + stu.getId() + "的信息更新索引失败！");
        }
    }

    public void delete(String id) throws IOException {
        DeleteRequest request = new DeleteRequest(ElasticConfig.INDEX, id);
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        ReplicationResponse.ShardInfo shardInfo = response.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
            System.out.println("文档ID为:" + id + "的信息删除失败！");
        } else {
            System.out.println("文档ID为:" + id + "的信息删除成功！");
        }
    }

    /**
     * 前端关键词模糊匹配
     */
    public List suggest(String prefix) throws IOException {
        //查询请求
        SearchRequest searchRequest = new SearchRequest(ElasticConfig.INDEX);
        //查询请求参数构造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        CompletionSuggestionBuilder completionSuggestionBuilder = SuggestBuilders.completionSuggestion("suggest").prefix(prefix).size(5);
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("stu_suggest", completionSuggestionBuilder);
        searchSourceBuilder.suggest(suggestBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        Suggest suggest = searchResponse.getSuggest();
        if (suggest == null) return new ArrayList<>();
        Suggest.Suggestion result = suggest.getSuggestion("stu_suggest");
        int maxSuggest = 0;
        Set<String> suggestSet = new HashSet<>();
        for (Object term : result.getEntries()) {
            if (term instanceof CompletionSuggestion.Entry) {
                CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) term;
                if (item.getOptions().isEmpty()) {
                    continue;
                }
                for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
                    String tip = option.getText().string();
                    if (suggestSet.contains(tip)) {
                        continue;
                    }
                    suggestSet.add(tip);
                    maxSuggest++;
                }
            }
            if (maxSuggest > 5) {//只获取前5条建议
                break;
            }
        }
        List<String> suggests = Lists.newArrayList(suggestSet.toArray(new String[]{}));
        return suggests;
    }

    //补全suggests字段
    public boolean addSuggest(Student stu) throws IOException {
        AnalyzeRequest request = new AnalyzeRequest();
        request.text(stu.getProfessional(),stu.getAddr(),stu.getDegree(),stu.getUniversity());
        request.analyzer("ik_smart");
        AnalyzeResponse response = client.indices().analyze(request, RequestOptions.DEFAULT);
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
        if (tokens == null) {
            System.err.println("id为：" + stu.getId() + "的对象不能进行分词，请查找原因！");
            return false;
        }
        List<StudentSuggest> suggests = new ArrayList<>();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            // 排序数字类型 || 小于2个字符的分词结果
            if ("<NUM>".equals(token.getType()) || token.getTerm().length() < 2) {
                continue;
            }
            StudentSuggest suggest = new StudentSuggest();
            suggest.setInput(token.getTerm());
            suggests.add(suggest);
        }
        //把名字、学校、民族单独加到suggest提示中
        StudentSuggest nameSuggest = new StudentSuggest();
        nameSuggest.setInput(stu.getName());
        StudentSuggest minzuSuggest = new StudentSuggest();
        minzuSuggest.setInput(stu.getMinzu());

        suggests.add(nameSuggest);
        suggests.add(minzuSuggest);
        stu.setSuggest(suggests);
        return true;
    }

}


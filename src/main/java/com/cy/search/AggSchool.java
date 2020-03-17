package com.cy.search;

import com.cy.mapper.StudentMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: 𝓛.𝓕.𝓠-0416
 */
@Service
public class AggSchool {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private StudentMapper mapper;

    /**
     * 学校数据聚合
     * index:0-默认（地大），1-女生多，2-专业多，3-毕业生多
     */
    public Map aggSchool(String index) throws IOException {
        Map map = null;
        switch (index) {
            case "0":
                map = initial();
                break;
            default:
                map = more(index);
                break;
        }
        return map;
    }

    //默认
    public Map initial() throws IOException {
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        boolQuery.must(QueryBuilders.matchPhraseQuery("university", "中国地质大学(武汉)"));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQuery);
        sourceBuilder.from(0);//索引位置
        sourceBuilder.size(mapper.count());//返回数目
        TermsAggregationBuilder nv = AggregationBuilders
                .terms("nv").field("gender");
        TermsAggregationBuilder zy = AggregationBuilders
                .terms("zy").field("professional.keyword");
        TermsAggregationBuilder degree = AggregationBuilders
                .terms("degree").field("degree.keyword");
        sourceBuilder.aggregation(nv).aggregation(zy).aggregation(degree);
        System.out.println("Initial-sourceBuilder:" + sourceBuilder);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        //获取该学校学生总人数
        SearchHits hits = response.getHits();
        long rs = hits.getTotalHits().value;
        //聚合分析
        Aggregations aggregations = response.getAggregations();
        List<Aggregation> list = aggregations.asList();
        System.out.println("Initial聚合分析的个数：" + list.size());
        //返回参数
        long numNv = 0;
        long numBy = 0;
        long numZy = 0;
        for (Aggregation agg : list) {
            //获取专业数目，因为专业数需要单独求类型个数
            if (agg.getName().equals("zy")) {
                numZy = ((Terms) agg).getBuckets().size();
            }
            //获取女生数、毕业生数
            for (Terms.Bucket bucket : ((Terms) agg).getBuckets()) {
                String key = bucket.getKey().toString();
                switch (key) {
                    case "女":
                        numNv = bucket.getDocCount();
                        break;
                    case "大学四年级":
                        numBy = bucket.getDocCount();
                        break;
                }
            }
        }
        //最后把各自聚合结果返回
        Map map = new HashMap();
        map.put("numNv", numNv);
        map.put("numBy", numBy);
        map.put("numZy", numZy);
        map.put("rs", rs);
        map.put("school", "中国地质大学(武汉)");
        return map;
    }

    //女生多
    public Map more(String index) throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(0);//索引位置
        sourceBuilder.size(mapper.count());//返回数目
        TermsAggregationBuilder university = AggregationBuilders
                .terms("university").field("university.keyword").size(20);
        TermsAggregationBuilder nv = AggregationBuilders
                .terms("nv").field("gender").size(2);
        CardinalityAggregationBuilder zy = AggregationBuilders
                .cardinality("zy").field("professional.keyword");//cardinality相当于sql的distinct
        TermsAggregationBuilder degree = AggregationBuilders
                .terms("degree").field("degree.keyword").size(4);

        university.subAggregation(nv).subAggregation(zy).subAggregation(degree);//添加子聚合
        sourceBuilder.aggregation(university);
        System.out.println("MoreGirl-sourceBuilder:" + sourceBuilder);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        Aggregations aggregations = response.getAggregations();
        List<Aggregation> asList = aggregations.asList();
        System.out.println("MoreGirl聚合分析的个数：" + asList.size());
        //返回参数
        Aggregation agg = asList.get(0);//因为university的聚合类型为terms，所以不用判断类型了
        Map map = new HashMap();
        List list = new ArrayList();
        for (Terms.Bucket bucket : ((Terms) agg).getBuckets()) {
            long rs = bucket.getDocCount();//该校学生总人数
            String school = bucket.getKey().toString();//学校名称
            Aggregations aggs = bucket.getAggregations();
            long numZy = 0;
            long numNv = 0;
            long numBy = 0;
            for (Aggregation aggregation : aggs.asList()) {
                String type = aggregation.getType();
                if (type.equals(CardinalityAggregationBuilder.NAME)) {
                    numZy = ((Cardinality) aggregation).getValue();
                } else {
                    for (Terms.Bucket bk : ((Terms) aggregation).getBuckets()) {
                        switch (bk.getKey().toString()) {
                            case "女":
                                numNv = bk.getDocCount();
                                break;
                            case "大学四年级":
                                numBy = bk.getDocCount();
                                break;
                        }
                    }
                }
            }
            map.put("numNv", numNv);
            map.put("numBy", numBy);
            map.put("numZy", numZy);
            map.put("school", school);
            map.put("rs", rs);
            list.add(map);
        }
        for (int i = 0; i < list.size(); i++) {
            Map m = (Map) (list.get(i));
            m.get("numNv");
        }

        //判断index，决定返回内容
        switch (index){
            case "1":

        }
        return null;

    }


}

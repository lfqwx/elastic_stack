layui.use(['element', 'layer', 'jquery', 'form'], function () {
    var $ = layui.jquery,
        element = layui.element, //Tab的切换功能，切换事件监听等，需要依赖element模块
        layer = layui.layer,
        form = layui.form;

    //默认卡片
    //后端请求数据
    $.get('/schoolAgg',{index:0},function (res) {
        change(0,res.data);
    });
    //监听卡片切换
    element.on('tab(test)', function (elem) {
        location.hash = 'test=' + $(this).attr('lay-id');
        //后端请求数据
        $.get('/schoolAgg',{index:elem.index},function (res) {
            change(elem.index,res.data);
        });
    });

    //卡片内容改变,data为后端返回的数据
    function change(index,data) {
        $('#xx_mc').text(data.school);
        $('#rs').text('学生总人数：'+data.rs+'名');
        $('#nv').text('女生数：'+data.numNv+'名');
        $('#by').text('毕业生数：'+data.numBy+'名');
        $('#zy').text('专业数：'+data.numZy+'门');
    }

    //监听指定开关
    form.on('switch(switchTest)', function (data) {
        layer.msg('开关checked：' + (this.checked ? 'true' : 'false'), {
            offset: '6px'
        });
        layer.tips('温馨提示：请注意开关状态的文字可以随意定义，而不仅仅是ON|OFF', data.othis)
    });
});
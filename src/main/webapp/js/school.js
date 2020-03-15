layui.use(['element', 'layer','jquery','form'], function () {
    var $ = layui.jquery,
        element = layui.element, //Tab的切换功能，切换事件监听等，需要依赖element模块
        layer = layui.layer,
        form = layui.form;

    //Hash地址的定位
    var layid = location.hash.replace(/^#test=/, '');
    element.tabChange('test', layid);

    //刷新以后定位不变,保持数据不变化
    element.on('tab(test)', function (elem) {
        location.hash = 'test=' + $(this).attr('lay-id');;
        change(elem.index)
    });
    //监听指定开关
    form.on('switch(switchTest)', function(data){
        layer.msg('开关checked：'+ (this.checked ? 'true' : 'false'), {
            offset: '6px'
        });
        layer.tips('温馨提示：请注意开关状态的文字可以随意定义，而不仅仅是ON|OFF', data.othis)
    });

    //Tab切换事件
    $('#xx_mc').text('民族大学');
    $('#data').text("学生122名");
    function change(index){
        switch (index) {
            case 0:
                $('#xx_mc').text('民族大学');
                $('#data').text("学生122名");
                break;
            case 1:
                $('#xx_mc').text('地质大学');
                $('#data').text("学生12名");
                break;
            default:
                $('#xx_mc').text('华中农业大学');
                $('#data').text("学生18名");
                break;
        }
    }
});
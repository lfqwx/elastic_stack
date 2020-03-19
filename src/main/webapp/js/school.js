layui.use(['element', 'layer', 'jquery'], function () {
    var $ = layui.jquery,
        element = layui.element, //Tab的切换功能，切换事件监听等，需要依赖element模块
        layer = layui.layer;


    //默认卡片
    $.get('/schoolAgg', {index: 0}, function (res) {
        change(0, res.data);
    });
    //监听卡片切换
    element.on('tab(test)', function (elem) {
        location.hash = 'test=' + $(this).attr('lay-id');
        //后端请求数据
        $.get('/schoolAgg', {index: elem.index}, function (res) {
            change(elem.index, res.data);
        });
    });

    //卡片内容改变,data为后端返回的数据
    function change(index, data) {
        $('#xx_mc').text(data.school);
        $('#rs').text('学生总人数：' + data.rs + '名');
        $('#nv').text('女生数：' + data.numNv + '名');
        $('#by').text('毕业生数：' + data.numBy + '名');
        $('#zy').text('专业数：' + data.numZy + '门');
    }
});
layui.use(['form', 'jquery'], function () {
    var $ = layui.jquery;
    //----百度地图初始化----
    var map = new BMap.Map("map");  // 创建Map实例
    map.centerAndZoom("华中农业大学", 12);      // 初始化地图,用城市名设置地图中心点
    map.enableScrollWheelZoom();   //启用滚轮放大缩小，默认禁用
    map.enableContinuousZoom();    //启用地图惯性拖拽，默认禁用
    //单击获取点击的经纬度
    // map.addEventListener("click",function(e){
    //     alert(e.point.lng + "," + e.point.lat);
    // });
    var myDrag = new BMapLib.RectangleZoom(map, {
        followText: "拉框操作"
    });
    myDrag.setCursor("default");


    //-------描绘区域----------

    $.get("/lon_lat", function (res) {
        drawRegion(map, res.data);
    });

    //拉框搜索
    layui.form.on('switch(switchTest)', function (data) {
        this.checked ? myDrag.open() : myDrag.close();//百度地图拉框搜索
        layer.tips(this.checked ? '开启' : '关闭', data.othis)
    });
});

//描绘区域
function drawRegion(map, regionList) {
    var lebels = [];
    var boundary = new BMap.Boundary();
    var polygonContext = {};
    var regionPoint;
    var textLabel;
    for (var i = 0; i < regionList.length; i++) {
        regionPoint = new BMap.Point(regionList[i].lon, regionList[i].lat);
        var textContent =
            '<p style="margin-top:10px;pointer-events: none">' + regionList[i].school + '</p>'
            + '<p style="pointer-events: none">共' + regionList[i].rs + '人</p>'
            + '<p style="pointer-events: none">女生' + regionList[i].nv + '人</p>'
            + '<p style="pointer-events: none">毕业生' + regionList[i].by + '人</p>'
            + '<p style="pointer-events: none">专业' + regionList[i].by + '门</p>';
        textLabel = new BMap.Label(textContent, {
            position: regionPoint,//标签位置
            offset: new BMap.Size(0, 20)//文本偏移量
        });

        textLabel.setStyle({
            height: '100px',
            width: '100px',
            color: '#fff',
            backgroundColor: 'lightcoral',
            border: '0px solid rgb(255, 0, 0)',
            borderRadius: "50%",
            fontWeight: 'bold',
            fontsize: '14px',
            display: 'inline',
            lineHeight: 'normal',
            textAlign: 'center',
            opacity: '0.8',
            zIndex: 2,
            overflow: 'hidden'
        });
        map.addOverlay(textLabel); // 将标签画在地图上
        lebels.push(textLabel);

        //记录覆盖物
        polygonContext[textLabel] = [];//点集合
        (function (textContent,school) {
            boundary.get(school, function (res) {
                var count = res.boundaries.length;//学校边界点集合长度
                if (count == 0) {
                    alert('未能获取到' + school + '边界！')
                    return;
                }
                for (var j = 0; j < count; j++) {
                    var polygon = new BMap.Polygon
                    {
                        res.boundaries[j],
                            {
                                strokeWeight: 2,
                                strokeColor: 'lightcoral',
                                fillOpacity: 0.3,
                                fillcolor: 'lightcoral'
                            }
                    };
                    map.addOverlay(polygon);
                    polygonContext[textContent].push(polygon);
                }

            });
        })(textContent,regionList[i].school);


    }
}
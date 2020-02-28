<html>
<head>
    <meta charset="UTF-8">
    <title>Freemarker入门Demo</title>
</head>
<body>
<#include "head.ftl">
<#--注释, 不会被输出-->
<!--html注释, 会被输出到源码-->
${name}, 你好, ${content} <br/>

<#assign contactMan={"name":"张三","address":"千户小区"}>
联系人姓名: ${contactMan.name}, 联系人地址: ${contactMan.address} <br>

<#if success==2>
    您已通过实名认证
    <#elseif success==3>
    您未通过实名认证
    哈哈哈
    啦啦啦
    <#else>
    刘辟
</#if><br>

<#list goodsList as good>
    ${good_index+1}商品名称: ${good.name}  商品价格: ${good.price}<br>
</#list>
共${goodsList?size}种商品. <br>

<#assign cop="{'name':'包平安', 'number':'54sb'}">
<#assign cop=cop?eval>

警察姓名: ${cop.name} 警号: ${cop.number}<br>

今天是: ${today?date}<br>
现在是: ${today?time}<br>
现在是: ${today?datetime}<br>
我说现在是: ${today?string("yyyy年MM月dd日 HH时mm分ss秒")}<br>

积分: ${point}<br>
转字符串: ${point?c}<br>

<#if aaa??>
    aaa有值
    <#else>
    aaa没值
</#if>
<br>
${bbb!"bbb没值"}
<br>
<#assign today=today?datetime tomorrow=tomorrow?datetime>
<#if today lt tomorrow>
    明天来啦
    <#else>
    今天来啦
</#if>

<#if str1 = str2>
    相等
<#else>
    不相等
</#if>

</body>

</html>
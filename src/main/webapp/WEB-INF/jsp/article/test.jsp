<%--
  Created by IntelliJ IDEA.
  User: 1
  Date: 2020/2/26
  Time: 14:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script type="text/javascript" src="http://lib.sinaapp.com/js/jquery/2.0.3/jquery-2.0.3.min.js"></script>
    <script type="text/javascript">
        var pageNumber = 1;
        var pageSize = 10;
        var base = '<%=request.getAttribute("base")%>';
        function article_reload() {
            $.ajax({
                url : base + "/article/query",
                data : $("#article_query_form").serialize(),
                dataType : "json",
                success : function(data) {
                    console.log(data);
                    $("#article_count").html("共"+data.pager.recordCount+"篇文章, 总计"+data.pager.pageCount+"页");
                    var list_html = "";
                    console.log(data.list);
                    for (var i=0;i<data.list.length;i++) {
                        var article = data.list[i];
                        console.log(article);
                        var tmp = "\n<p>" + article.id + " " + article.userId + " " + article.subject+ + " " + article.type + " " + article.readType + " " + article.content + " "
                            + " <button onclick='article_delete(" + article.id +");'>删除</button> "
                            + "</p>";
                        list_html += tmp;
                    }
                    $("#article_list").html(list_html);
                }
            });
        }
        $(function() {
            article_reload();
            $("#article_query_btn").click(function() {
                article_reload();
            });
            $("#article_save_btn").click(function() {
                $.ajax({
                    url : base + "/article/save"+"?status=0",
                    data : $("#article_edit_form").serialize(),
                    dataType : "json",
                    success : function(data) {
                        if (data.ok) {
                            alert("保存成功");
                        } else {
                            alert(data.msg);
                        }
                    }
                });
            });
            $("#article_pub_btn").click(function() {
                $.ajax({
                    url : base + "/article/save"+"?status=1",
                    data : $("#article_edit_form").serialize(),
                    dataType : "json",
                    success : function(data) {
                        if (data.ok) {
                            alert("发布成功");
                        } else {
                            alert(data.msg);
                        }
                    }
                });
            });
            $("#article_create_btn").click(function() {
                window.location=base+"/article/get?articleId=0";
            });
        });
        function article_delete(articleId) {
            var s = prompt("请输入y确认删除");
            if (s == "y") {
                $.ajax({
                    url : base + "/article/delete",
                    data : {"articleId":articleId},
                    dataType : "json",
                    success : function (data) {
                        if (data.ok) {
                            article_reload();
                            alert("删除成功");
                        } else {
                            alert(data.msg);
                        }
                    }
                });
            }
        };
    </script>
</head>
<body>
<div>
    <form action="#" id="article_query_form">
        用户id<input type="text" name="userId" value="1">
        页数<input type="text" name="pageNumber" value="1">
        每页<input type="text" name="pageSize" value="10">
    </form>
    <button id="article_query_btn">查询</button>
    <p>---------------------------------------------------------------</p>
    <p id="article_count"></p>
    <div id="article_list">

    </div>
</div>
<div>
    <p>---------------------------------------------------------------</p>
</div>

<div id="article_edit">
    <form action="#" id="article_edit_form">
        <input hidden="hidden" name="id" value="${obj.id}">
        <input hidden="hidden" name="userId" value="${sessionScope.ident}">
        标题<input name="subject">
        种类<select name="type">
                <option value ="动态分享">动态分享</option>
                <option value ="求助">求助</option>
                <option value ="科普">科普</option>
            </select>
        发布形式<select name="readType">
                <option value ="私密">私密</option>
                <option value ="公开">公开</option>
                <option value ="关注可见">关注可见</option>
            </select>
        内容<input name="content">
        匿名<select name="annoymous">
                <option value ="true">匿名</option>
                <option value ="false">公开</option>
            </select>
    </form>
    <button id="article_save_btn">保存</button>
    <button id="article_pub_btn">发布</button>
    <button id="article_create_btn">创建</button>
</div>
</body>
</html>

<%--
  Created by IntelliJ IDEA.
  User: 1
  Date: 2020/2/25
  Time: 13:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>宠物列表</title>
    <script type="text/javascript" src="http://lib.sinaapp.com/js/jquery/2.0.3/jquery-2.0.3.min.js"></script>
    <script type="text/javascript">
        var pageNumber = 1;
        var pageSize = 10;
        var base = '<%=request.getAttribute("base")%>';
        function pet_reload() {
            $.ajax({
                url : base + "/pet/query",
                data : $("#pet_query_form").serialize(),
                dataType : "json",
                success : function(data) {
                    console.log(data);
                    $("#pet_count").html("共"+data.pager.recordCount+"个宠物, 总计"+data.pager.pageCount+"页");
                    var list_html = "";
                    console.log(data.list);
                    for (var i=0;i<data.list.length;i++) {
                        var pet = data.list[i];
                        console.log(pet);
                        var tmp = "\n<p>" + pet.id + " " + pet.petName+" "+pet.gender+" "+pet.type+" "+pet.birthTime+" "+pet.adoptionTime+" "+pet.sterilized+" "+pet.description
                            + " <button onclick='pet_update(" + pet.id +");'>修改</button> "
                            + " <button onclick='pet_delete(" + pet.id +");'>删除</button> "
                            + "</p>";
                        list_html += tmp;
                    }
                    $("#pet_list").html(list_html);
                }
            });
        }
        $(function() {
            pet_reload();
            $("#pet_query_btn").click(function() {
                pet_reload();
            });
            $("#pet_add_btn").click(function() {
                $.ajax({
                    url : base + "/pet/add",
                    data : $("#pet_add_form").serialize(),
                    dataType : "json",
                    success : function(data) {
                        if (data.ok) {
                            pet_reload();
                            alert("添加成功");
                        } else {
                            alert(data.msg);
                        }
                    }
                });
            });
        });
        function pet_update(petId) {
            var passwd = prompt("请输入新的密码");
            if (passwd) {
                $.ajax({
                    url : base + "/pet/update",
                    data : {"id":petId,"password":passwd},
                    dataType : "json",
                    success : function (data) {
                        if (data.ok) {
                            pet_reload();
                            alert("修改成功");
                        } else {
                            alert(data.msg);
                        }
                    }
                });
            }
        };
        function pet_delete(petId) {
            var s = prompt("请输入y确认删除");
            if (s == "y") {
                $.ajax({
                    url : base + "/pet/delete",
                    data : {"id":petId},
                    dataType : "json",
                    success : function (data) {
                        if (data.ok) {
                            user_reload();
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
    <form action="#" id="pet_query_form">
        页数<input type="text" name="pageNumber" value="1">
        每页<input type="text" name="pageSize" value="10">
    </form>
    <button id="pet_query_btn">查询</button>
    <p>---------------------------------------------------------------</p>
    <p id="pet_count"></p>
    <div id="pet_list">

    </div>
</div>
<div>
    <p>---------------------------------------------------------------</p>
</div>
<div id="pet_add">
    <form action="#" id="pet_add_form">
        昵称<input name="petName">
        性别<select name="gender">
                <option value ="GG">GG</option>
                <option value ="MM">MM</option>
            </select>
        品种<select name="type">
                <option value ="英短">英短</option>
                <option value ="美短">美短</option>
                <option value ="其他">其他</option>
            </select>
        出生日期<input type="date" value="2020-02-25" name="birthTime"/>
        到家时间<input type="date" value="2020-02-25" name="adoptionTime"/>
        绝育情况<select name="sterilized">
                    <option value ="已绝育">已绝育</option>
                    <option value ="未绝育">未绝育</option>
                </select>
        简介<input name="description">
    </form>
    <button id="pet_add_btn">新增</button>
</div>
</body>
</html>

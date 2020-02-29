<%--
  Created by IntelliJ IDEA.
  User: 1
  Date: 2020/2/28
  Time: 20:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Title</title>
    <script type="text/javascript" src="http://lib.sinaapp.com/js/jquery/2.0.3/jquery-2.0.3.min.js"></script>
    <script type="text/javascript">
        var base = '<%=request.getAttribute("base")%>';
        console.log(${obj});
    </script>
</head>
<body>
    <c:forEach items="${obj}" var="user">
        <div>
            头像<img id="avatar" src="${base}/user/profile/avatar?userId=${user.userId}">
            <form action="#" id="user_profile${user.userId}" method="post">
                <div>
                    userId:<c:out value="${user.userId}"></c:out>
                </div>
                <div>
                    昵称:<input name="nickname" value="${user.nickname}">
                </div>
                <div>
                    性别:<input name="description" value="${user.description}">
                </div>
                <div>
                    自我介绍:<input name="praise" value="${user.praise}">
                </div>
                <div>
                    地理位置:<input name="follower" value="${user.follower}">
                </div>
            </form>
            <c:choose>
                <c:when test="${user.id>0}">
                    <script type="text/javascript">
                        function remove(fid,id) {
                            $.ajax({
                                url : "${base}/follow/remove?userId="+id+"&followId="+fid,
                                type : "POST",
                                dataType : "json",
                                success : function (data) {
                                    if (data.ok) {
                                        $("#follow").html("关注");
                                        $("#follow").onclick="add("+id+")";
                                    } else {
                                        alert(data.msg);
                                    }
                                }
                            });
                        }
                    </script>
                    <button id="follow" type="button" onclick="remove(${user.id},${user.userId});return false;">取消关注</button>
                </c:when>
                <c:otherwise>
                    <script type="text/javascript">
                        function add(id) {
                            $.ajax({
                                url : "${base}/follow/add?userId="+id,
                                type : "POST",
                                dataType : "json",
                                success : function (data) {
                                    if (data.ok) {
                                        $("#follow").html("取消关注");
                                        $("#follow").onclick="remove("+data.data.id+","+data.data.to+")";
                                    } else {
                                        alert(data.msg);
                                    }
                                }
                            });
                        }
                    </script>
                    <button id="follow" type="button" onclick="add(${user.userId});return false;">关注</button>
                </c:otherwise>
            </c:choose>
            <button type="button" id="user_profile_btn${user.userId}">更新</button>
        </div>
    </c:forEach>
</body>
</html>

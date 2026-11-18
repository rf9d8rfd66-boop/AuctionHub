<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Управление пользователями</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

      <link rel="stylesheet"
            href="${pageContext.request.contextPath}/css/admin.css">

</head>
<body>

<h1>Управление пользователями</h1>

<a href="${pageContext.request.contextPath}/admin">
    Назад в админ-панель
</a>

<c:if test="${not empty userMessage}">
    <p style="color: ${userMessageType == 'success' ? 'green' : 'red'}">
        <c:out value="${userMessage}"/>
    </p>
</c:if>

<hr>

<h2>Список пользователей</h2>

<c:choose>

    <c:when test="${empty users}">
        <p>Пользователей пока нет.</p>
    </c:when>

    <c:otherwise>

        <table border="1" cellpadding="8" cellspacing="0">

            <thead>
            <tr>
                <th>ID</th>
                <th>Логин</th>
                <th>Email</th>
                <th>Телефон</th>
                <th>Дата регистрации</th>
                <th>Роль</th>
                <th>Действие</th>
            </tr>
            </thead>

            <tbody>

            <c:forEach var="user" items="${users}">

                <tr>
                    <td>${user.id}</td>

                    <td>
                        <c:out value="${user.username}"/>
                    </td>

                    <td>
                        <c:out value="${user.email}"/>
                    </td>

                    <td>
                        <c:out value="${user.phone}"/>
                    </td>

                    <td>${user.createdAt}</td>

                    <td>
                        <c:choose>
                            <c:when test="${user.roleId == 1}">
                                Пользователь
                            </c:when>
                            <c:when test="${user.roleId == 2}">
                                Администратор
                            </c:when>
                        </c:choose>
                    </td>

                    <td>

                        <c:if test="${user.id != currentUserId}">

                            <form method="post"
                                  action="${pageContext.request.contextPath}/admin/users">

                                <input type="hidden"
                                       name="userId"
                                       value="${user.id}">

                                <select name="roleId">

                                    <option value="1"
                                            ${user.roleId == 1 ? 'selected' : ''}>
                                        Пользователь
                                    </option>

                                    <option value="2"
                                            ${user.roleId == 2 ? 'selected' : ''}>
                                        Администратор
                                    </option>

                                </select>

                                <button type="submit">
                                    Изменить роль
                                </button>

                            </form>

                        </c:if>

                        <c:if test="${user.id == currentUserId}">
                            Это вы
                        </c:if>

                    </td>
                </tr>

            </c:forEach>

            </tbody>

        </table>

    </c:otherwise>

</c:choose>

</body>
</html>
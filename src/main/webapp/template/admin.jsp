<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Панель администратора — AuctionHub</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

      <link rel="stylesheet"
            href="${pageContext.request.contextPath}/css/admin.css">

</head>

<body>

<div class="admin-container">

    <h1>Панель администратора</h1>

    <p>Управление платформой AuctionHub</p>

    <p>
        <a href="${pageContext.request.contextPath}/admin/categories">
            Управление категориями
        </a>
    </p>

    <p>
        <a href="${pageContext.request.contextPath}/admin/auctions">
            Управление аукционами
        </a>
    </p>

    <p>
        <a href="${pageContext.request.contextPath}/admin/users">
            Управление пользователями
        </a>
    </p>

    <div class="admin-card">

        <h2>Пользователи</h2>

        <p>
            Всего зарегистрировано:
            <strong><c:out value="${usersCount}"/></strong>
        </p>

        <table>

            <thead>
            <tr>
                <th>ID</th>
                <th>Логин</th>
                <th>Email</th>
                <th>Телефон</th>
                <th>Роль</th>
                <th>Дата регистрации</th>
            </tr>
            </thead>

            <tbody>

            <c:forEach var="user" items="${users}">

                <tr>
                    <td><c:out value="${user.id}"/></td>

                    <td><c:out value="${user.username}"/></td>

                    <td><c:out value="${user.email}"/></td>

                    <td>
                        <c:out value="${user.phone}"/>
                    </td>

                    <td>
                        <c:choose>
                            <c:when test="${user.roleId == 2}">
                                <span class="badge admin-badge">
                                    Администратор
                                </span>
                            </c:when>

                            <c:otherwise>
                                <span class="badge">
                                    Пользователь
                                </span>
                            </c:otherwise>
                        </c:choose>
                    </td>

                    <td>
                        <c:out value="${user.createdAt}"/>
                    </td>
                </tr>

            </c:forEach>

            </tbody>

        </table>

    </div>

    <p>
        <a href="${pageContext.request.contextPath}/profile">
            Вернуться в профиль
        </a>
    </p>

</div>

</body>
</html>
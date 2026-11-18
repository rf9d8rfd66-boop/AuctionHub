<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Вход</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>
<body>

<h1>Вход в AuctionHub</h1>

<c:if test="${not empty error}">
    <p class="error-message">
        <c:out value="${error}"/>
    </p>
</c:if>

<c:if test="${not empty success}">
    <p class="success-message">
        <c:out value="${success}"/>
    </p>
</c:if>

<form action="${pageContext.request.contextPath}/login" method="post">

    <div>
        <label>Логин:</label>
        <input type="text"
               name="username"
               value="${username}"
               required>
    </div>



    <div>
        <label>Пароль:</label>
        <input type="password"
               name="password"
               required>
    </div>



    <button type="submit">Войти</button>

</form>

<p>
    Нет аккаунта?
    <a href="${pageContext.request.contextPath}/register">
        Зарегистрироваться
    </a>
</p>


<a href="${pageContext.request.contextPath}/logout">
    Выйти из аккаунта
</a>

</body>
</html>
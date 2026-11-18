<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Регистрация — AuctionHub</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>
<body>

<h1>Регистрация в AuctionHub</h1>

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

<form action="${pageContext.request.contextPath}/register" method="post">

    <div>
        <label for="username">Логин:</label>
        <input type="text" id="username" name="username" required maxlength="50">
    </div>



    <div>
        <label for="email">Email:</label>
        <input type="email" id="email" name="email"  required maxlength="100">
    </div>



    <div>
        <label for="phone">Телефон:</label>
        <input type="text" name="phone" value="${phone}" maxlength="20">
    </div>



    <div>
        <label for="password">Пароль:</label>
        <input type="password" id="password" name="password" required>
    </div>



    <div>
        <label for="confirmPassword">Повторите пароль:</label>
        <input type="password" id="confirmPassword" name="confirmPassword" required>
    </div>



    <button type="submit">Зарегистрироваться</button>

</form>

<p>
    Уже есть аккаунт?
    <a href="${pageContext.request.contextPath}/login">Войти</a>
</p>

</body>
</html>
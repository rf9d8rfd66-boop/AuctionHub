<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Ошибка — AuctionHub</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="container">
    <div class="error-page">

        <h1>Что-то пошло не так</h1>

        <h2>
            Ошибка ${requestScope['jakarta.servlet.error.status_code']}
        </h2>

        <c:set var="status"
               value="${requestScope['jakarta.servlet.error.status_code']}"/>

        <c:choose>
            <c:when test="${status == 400}">
                <p>Некорректный запрос. Проверьте введённые данные.</p>
            </c:when>

            <c:when test="${status == 403}">
                <p>У вас нет доступа к этой странице.</p>
            </c:when>

            <c:when test="${status == 404}">
                <p>Запрашиваемая страница не найдена.</p>
            </c:when>

            <c:when test="${status == 405}">
                <p>Этот способ обращения к странице не поддерживается.</p>
            </c:when>

            <c:otherwise>
                <p>На сервере произошла внутренняя ошибка. Попробуйте позже.</p>
            </c:otherwise>
        </c:choose>

        <a href="${pageContext.request.contextPath}/profile"
           class="btn">
            На главную
        </a>

    </div>
</div>

</body>
</html>
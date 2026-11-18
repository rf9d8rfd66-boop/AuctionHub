<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Мои ставки — AuctionHub</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>
<body>

<h1>Мои ставки</h1>

<nav>
    <a href="${pageContext.request.contextPath}/profile">Личный кабинет</a>
    |
    <a href="${pageContext.request.contextPath}/catalog">Каталог</a>
</nav>

<hr>

<c:choose>
    <c:when test="${not empty bids}">

        <c:forEach var="bid" items="${bids}">

            <div>
                <h3>
                    Аукцион №<c:out value="${bid.auctionId}"/>
                </h3>

                <p>
                    Размер ставки:
                    <c:out value="${bid.amount}"/> ₽
                </p>

                <p>
                    Дата ставки:
                    <c:out value="${bid.createdAt}"/>
                </p>

                <a href="${pageContext.request.contextPath}/auction/view?id=${bid.auctionId}">
                    Перейти к аукциону
                </a>
            </div>

            <hr>

        </c:forEach>

    </c:when>

    <c:otherwise>
        <p>Вы пока не делали ставок.</p>
    </c:otherwise>
</c:choose>

</body>
</html>
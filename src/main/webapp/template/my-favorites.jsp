<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Избранное — AuctionHub</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>
<body>

<h1>Избранное</h1>

<nav>
    <a href="${pageContext.request.contextPath}/profile">Личный кабинет</a>
    |
    <a href="${pageContext.request.contextPath}/catalog">Каталог</a>
</nav>

<hr>

<c:choose>
    <c:when test="${not empty favoriteAuctions}">

        <c:forEach var="favorite" items="${favoriteAuctions}">

            <div>

                <c:if test="${not empty favorite.imagePath}">
                    <img
                        class = "auction-photo"
                        src="${pageContext.request.contextPath}/images/${fn:substringAfter(favorite.imagePath, 'uploads/')}"
                        alt="Фотография товара"
                        width="200"
                    >
                </c:if>

                <h3>
                    <c:out value="${favorite.title}"/>
                </h3>

                <p>
                    Город: <c:out value="${favorite.city}"/>
                </p>

                <p>
                    Текущая цена:
                    <c:out value="${favorite.currentPrice}"/> ₽
                </p>

                <p>
                    Статус:
                    <c:out value="${favorite.status}"/>
                </p>

                <p>
                    Окончание:
                    <c:out value="${favorite.endTime}"/>
                </p>

                <a href="${pageContext.request.contextPath}/auction/view?id=${favorite.auctionId}">
                    Подробнее об аукционе
                </a>

                <form action="${pageContext.request.contextPath}/favorites"
                      method="post">

                    <input type="hidden"
                           name="auctionId"
                           value="${favorite.auctionId}">

                    <input type="hidden"
                           name="action"
                           value="remove">

                    <input type="hidden"
                           name="returnTo"
                           value="my-favorites">

                    <button type="submit">
                        Удалить из избранного
                    </button>

                </form>

            </div>

            <hr>

        </c:forEach>

    </c:when>

    <c:otherwise>
        <p>В избранном пока ничего нет.</p>
    </c:otherwise>
</c:choose>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/photo-viewer.css">

<script src="${pageContext.request.contextPath}/js/photo-viewer.js"></script>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Мои объявления — AuctionHub</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>
<body>

<h1>Мои объявления</h1>

<a href="${pageContext.request.contextPath}/item/create">
    Создать аукцион
</a>

<nav>
    <a href="${pageContext.request.contextPath}/profile">Личный кабинет</a>
    |
    <a href="${pageContext.request.contextPath}/catalog">Каталог</a>
</nav>

<hr>

<c:if test="${not empty deleteMessage}">
    <p>
        <strong><c:out value="${deleteMessage}"/></strong>
    </p>
</c:if>

<c:choose>
    <c:when test="${not empty items}">

        <c:forEach var="item" items="${items}">

            <div>
                <h3>
                    <c:out value="${item.title}"/>
                </h3>

                <p>
                    <c:out value="${item.description}"/>
                </p>

                <p>
                    Город: <c:out value="${item.city}"/>
                </p>

                <c:if test="${not empty itemImages[item.id]}">

                    <h4>Фотографии:</h4>

                    <c:forEach var="image" items="${itemImages[item.id]}">
                        <img
                            class = "auction-photo"
                            src="${pageContext.request.contextPath}/images/${fn:substringAfter(image.imagePath, 'uploads/')}"
                            alt="Фотография товара"
                            width="200"
                        >
                    </c:forEach>

                </c:if>

                <c:if test="${not empty itemAuctions[item.id]}">
                    <p>
                        <a href="${pageContext.request.contextPath}/auction/view?id=${itemAuctions[item.id].id}">
                            Подробнее об аукционе
                        </a>
                    </p>
                </c:if>

                <c:if test="${deletableItemIds[item.id]}">

                    <form action="${pageContext.request.contextPath}/my-items"
                          method="post"
                          data-confirm="Вы действительно хотите удалить это объявление?">

                        <input type="hidden"
                               name="itemId"
                               value="${item.id}">

                        <button type="submit">
                            Удалить объявление
                        </button>

                    </form>

                </c:if>

            </div>

            <hr>

        </c:forEach>

    </c:when>

    <c:otherwise>
        <p>У вас пока нет объявлений.</p>
    </c:otherwise>
</c:choose>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/photo-viewer.css">

<script src="${pageContext.request.contextPath}/js/photo-viewer.js"></script>

<script src="${pageContext.request.contextPath}/js/form-confirm.js"></script>

</body>
</html>
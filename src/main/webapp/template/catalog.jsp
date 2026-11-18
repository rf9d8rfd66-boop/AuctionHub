<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>AuctionHub — Каталог аукционов</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>
<body>

<h1>AuctionHub</h1>
<h2>Каталог аукционов</h2>


<a href="${pageContext.request.contextPath}/profile">Личный кабинет</a>
    |
<a href="${pageContext.request.contextPath}/item/create">
    Создать аукцион
</a>

<form action="${pageContext.request.contextPath}/catalog" method="get">

    <h3>Поиск и фильтры</h3>

    <!-- Поиск по названию -->
    <div>
        <label for="q">Название товара:</label>
        <input
            type="text"
            id="q"
            name="q"
            placeholder="Например, iPhone"
            value="<c:out value='${filter.title}'/>">
    </div>

    <!-- Категория -->
    <div>
        <label for="categoryId">Категория:</label>

        <select name="categoryId" id="categoryId">
            <option value="">Все категории</option>

            <c:forEach var="category" items="${categories}">
                <option
                    value="${category.id}"
                    ${filter.categoryId == category.id ? 'selected' : ''}>
                    <c:out value="${category.name}"/>
                </option>
            </c:forEach>
        </select>
    </div>

    <!-- Город -->
    <div>
        <label for="city">Город:</label>
        <input
            type="text"
            id="city"
            name="city"
            placeholder="Например, Казань"
            value="<c:out value='${filter.city}'/>">
    </div>

    <!-- Минимальная цена -->
    <div>
        <label for="minPrice">Цена от:</label>
        <input
            type="number"
            id="minPrice"
            name="minPrice"
            min="0"
            step="0.01"
            placeholder="От"
            value="${filter.minPrice}">
    </div>

    <!-- Максимальная цена -->
    <div>
        <label for="maxPrice">Цена до:</label>
        <input
            type="number"
            id="maxPrice"
            name="maxPrice"
            min="0"
            step="0.01"
            placeholder="До"
            value="${filter.maxPrice}">
    </div>

    <!-- Статус -->
    <div>
        <label for="status">Статус аукциона:</label>

        <select name="status" id="status">
            <option value="">Все статусы</option>

            <option value="UPCOMING"
                ${filter.status == 'UPCOMING' ? 'selected' : ''}>
                Предстоящие
            </option>

            <option value="ACTIVE"
                ${filter.status == 'ACTIVE' ? 'selected' : ''}>
                Активные
            </option>

            <option value="COMPLETED"
                ${filter.status == 'COMPLETED' ? 'selected' : ''}>
                Завершённые
            </option>

            <option value="CANCELLED"
                ${filter.status == 'CANCELLED' ? 'selected' : ''}>
                Отменённые
            </option>
        </select>
    </div>

    <!-- Сортировка -->
    <div>
        <label for="sort">Сортировка:</label>

        <select name="sort" id="sort">

            <option value="newest"
                ${filter.sort == 'newest' ? 'selected' : ''}>
                Сначала новые
            </option>

            <option value="ending_soon"
                ${filter.sort == 'ending_soon' ? 'selected' : ''}>
                Скоро заканчиваются
            </option>

            <option value="price_asc"
                ${filter.sort == 'price_asc' ? 'selected' : ''}>
                Сначала дешевле
            </option>

            <option value="price_desc"
                ${filter.sort == 'price_desc' ? 'selected' : ''}>
                Сначала дороже
            </option>

        </select>
    </div>



    <button type="submit">Применить фильтры</button>

    <a href="${pageContext.request.contextPath}/catalog">
        Сбросить всё
    </a>

</form>

<c:if test="${not empty filterError}">
    <p class="error-message">
        <c:out value="${filterError}"/>
    </p>
</c:if>



<c:choose>
    <c:when test="${empty catalogItems}">
        <p>Пока нет доступных аукционов.</p>
    </c:when>

    <c:otherwise>
        <c:forEach var="auction" items="${catalogItems}">

            <div>
                <h3>
                    <c:out value="${auction.title}"/>
                </h3>

                <c:if test="${not empty auction.imagePath}">
                    <img
                        class = "auction-photo"
                        src="${pageContext.request.contextPath}/images/${fn:substringAfter(auction.imagePath, 'uploads/')}"
                        alt="Фотография товара"
                        width="200">
                </c:if>

                <p>Категория: <c:out value="${auction.categoryName}"/></p>
                <p>Город: <c:out value="${auction.city}"/></p>
                <p>Продавец: <c:out value="${auction.sellerUsername}"/></p>

                <p>
                    Текущая цена:
                    <c:out value="${auction.currentPrice}"/> ₽
                </p>

                <p>
                    Шаг ставки:
                    <c:out value="${auction.bidStep}"/> ₽
                </p>

                <p>Статус: <c:out value="${auction.status}"/></p>

                <c:choose>
                    <c:when test="${auction.status == 'UPCOMING'}">
                        <p>До начала торгов:</p>
                    </c:when>

                    <c:when test="${auction.status == 'ACTIVE'}">
                        <p>До окончания торгов:</p>
                    </c:when>
                </c:choose>

                <c:if test="${not empty auction.countdownTarget}">
                    <p class="auction-countdown"
                       data-target="${auction.countdownTarget}">
                        Загрузка таймера...
                    </p>
                </c:if>

                <p>
                    Окончание:
                    <c:out value="${auction.formattedEndTime}"/>
                </p>

                <a href="${pageContext.request.contextPath}/auction/view?id=${auction.auctionId}">
                    Подробнее об аукционе
                </a>

            </div>

            <hr>

        </c:forEach>
    </c:otherwise>
</c:choose>



<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/photo-viewer.css">

<script src="${pageContext.request.contextPath}/js/auction-timer.js"></script>

<script src="${pageContext.request.contextPath}/js/photo-viewer.js"></script>

</body>
</html>
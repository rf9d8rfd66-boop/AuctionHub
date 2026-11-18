<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Управление аукционами</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/admin.css">

</head>
<body>

<h1>Управление аукционами</h1>

<a href="${pageContext.request.contextPath}/admin">
    Назад в админ-панель
</a>

<c:if test="${not empty auctionMessage}">
    <p style="color: ${auctionMessageType == 'success' ? 'green' : 'red'}">
        <c:out value="${auctionMessage}"/>
    </p>
</c:if>

<hr>

<h2>Фильтр по статусу</h2>

<form method="get"
      action="${pageContext.request.contextPath}/admin/auctions">

    <select name="status">
        <option value="ALL"
                ${selectedStatus == 'ALL' ? 'selected' : ''}>
            Все аукционы
        </option>

        <option value="UPCOMING"
                ${selectedStatus == 'UPCOMING' ? 'selected' : ''}>
            Предстоящие
        </option>

        <option value="ACTIVE"
                ${selectedStatus == 'ACTIVE' ? 'selected' : ''}>
            Активные
        </option>

        <option value="COMPLETED"
                ${selectedStatus == 'COMPLETED' ? 'selected' : ''}>
            Завершённые
        </option>

        <option value="CANCELLED"
                ${selectedStatus == 'CANCELLED' ? 'selected' : ''}>
            Отменённые
        </option>
    </select>

    <button type="submit">Применить</button>
</form>

<hr>

<h2>Список аукционов</h2>

<c:choose>
    <c:when test="${empty auctions}">
        <p>Аукционы не найдены.</p>
    </c:when>

    <c:otherwise>

        <table border="1" cellpadding="8" cellspacing="0">

            <thead>
            <tr>
                <th>ID</th>
                <th>Товар</th>
                <th>Город</th>
                <th>Продавец</th>
                <th>Текущая цена</th>
                <th>Начало</th>
                <th>Окончание</th>
                <th>Статус</th>
                <th>Действия</th>
            </tr>
            </thead>

            <tbody>

            <c:forEach var="row" items="${auctions}">

                <tr>
                    <td>${row.auction.id}</td>

                    <td>
                        <c:out value="${row.title}"/>
                    </td>

                    <td>
                        <c:out value="${row.city}"/>
                    </td>

                    <td>
                        <c:out value="${row.sellerUsername}"/>
                    </td>

                    <td>${row.auction.currentPrice}</td>

                    <td>${row.formattedStartTime}</td>

                    <td>${row.formattedEndTime}</td>

                    <td>
                        <c:choose>
                            <c:when test="${row.effectiveStatus == 'UPCOMING'}">
                                Предстоящий
                            </c:when>
                            <c:when test="${row.effectiveStatus == 'ACTIVE'}">
                                Активный
                            </c:when>
                            <c:when test="${row.effectiveStatus == 'COMPLETED'}">
                                Завершённый
                            </c:when>
                            <c:when test="${row.effectiveStatus == 'CANCELLED'}">
                                Отменённый
                            </c:when>
                        </c:choose>
                    </td>

                    <td>
                        <c:if test="${row.effectiveStatus == 'UPCOMING'
                                     || row.effectiveStatus == 'ACTIVE'}">

                            <form method="post"
                                  action="${pageContext.request.contextPath}/admin/auctions"
                                  data-confirm="Вы действительно хотите отменить этот аукцион?">

                                <input type="hidden"
                                       name="action"
                                       value="cancel">

                                <input type="hidden"


name="auctionId"
                                       value="${row.auction.id}">

                                <button type="submit">
                                    Отменить
                                </button>

                            </form>

                        </c:if>
                    </td>
                </tr>

            </c:forEach>

            </tbody>
        </table>

    </c:otherwise>
</c:choose>

<script src="${pageContext.request.contextPath}/js/form-confirm.js"></script>

</body>
</html>
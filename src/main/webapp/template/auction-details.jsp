<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="ru">

<head>
    <meta charset="UTF-8">
    <title>
        <c:out value="${item.title}"/> — AuctionHub
    </title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>

<body>

<h1>
    <c:out value="${item.title}"/>
</h1>

<hr>

<h2>Фотографии товара</h2>

<c:choose>
    <c:when test="${not empty images}">

        <c:forEach var="image" items="${images}">

            <img
            class = "auction-photo"
                src="${pageContext.request.contextPath}/images/${fn:substringAfter(image.imagePath, 'uploads/')}"
                alt="Фотография товара"
                width="300"
            >

        </c:forEach>

    </c:when>

    <c:otherwise>
        <p>Фотографии отсутствуют.</p>
    </c:otherwise>
</c:choose>

<hr>

<h2>Описание товара</h2>

<p>
    <c:out value="${item.description}"/>
</p>

<p>
    <strong>Город:</strong>
    <c:out value="${item.city}"/>
</p>

<hr>

<h2>Информация об аукционе</h2>

<p>
    <strong>Начальная цена:</strong>
    <c:out value="${auction.startingPrice}"/> ₽
</p>

<p>
    <strong>Текущая цена:</strong>
    <c:out value="${auction.currentPrice}"/> ₽
</p>

<hr>

<h3>Сделать ставку</h3>

<c:if test="${not empty bidMessage}">
<p class="${bidSuccess ? 'success-message' : 'error-message'}">
        <c:out value="${bidMessage}"/>
    </p>
</c:if>

<c:choose>

    <c:when test="${canBid}">

        <p>
            Минимальная ставка:
            <c:out value="${minimumBid}"/> ₽
        </p>

        <form action="${pageContext.request.contextPath}/auction/bid"
              method="post">

            <input type="hidden"
                   name="auctionId"
                   value="${auction.id}">

            <label for="amount">Ваша ставка:</label>

            <input
                type="number"
                id="amount"
                name="amount"
                min="${minimumBid}"
                step="0.01"
                required>

            <button type="submit">Сделать ставку</button>

        </form>

    </c:when>

    <c:when test="${effectiveStatus == 'UPCOMING'}">
        <p>Торги ещё не начались.</p>
    </c:when>

    <c:when test="${effectiveStatus == 'COMPLETED'}">
        <p>Торги завершены.</p>
    </c:when>

    <c:when test="${effectiveStatus == 'CANCELLED'}">
        <p>Аукцион отменён.</p>
    </c:when>

    <c:otherwise>
        <p>Войдите в аккаунт, чтобы участвовать в торгах.</p>
    </c:otherwise>

</c:choose>

<hr>
<h3>История ставок</h3>

<c:choose>
    <c:when test="${not empty bids}">
        <table>
            <thead>
                <tr>
                    <th>Участник</th>
                    <th>Сумма ставки</th>
                    <th>Дата и время</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="bid" items="${bids}">
                    <tr>
                        <td><c:out value="${bid.username}"/></td>
                        <td><c:out value="${bid.amount}"/> ₽</td>
                        <td><c:out value="${bid.createdAt}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>

    <c:otherwise>
        <p>Пока никто не сделал ставку.</p>
    </c:otherwise>
</c:choose>

<hr>

<p>
    <strong>Шаг ставки:</strong>
    <c:out value="${auction.bidStep}"/> ₽
</p>

<p>
    <strong>Статус:</strong>
    <c:out value="${effectiveStatus}"/>
</p>

<c:if test="${not empty countdownTarget}">

    <h3>
        <c:out value="${countdownLabel}"/>
    </h3>

    <p id="auction-countdown"
       data-target="${countdownTarget}">
        Загрузка таймера...
    </p>

</c:if>

<p>
    <strong>Начало торгов:</strong>
    <c:out value="${formattedStartTime}"/>
</p>

<p>
    <strong>Окончание торгов:</strong>
    <c:out value="${formattedEndTime}"/>
</p>

<c:choose>
    <c:when test="${not empty sessionScope.userId}">
        <form action="${pageContext.request.contextPath}/favorites" method="post">
            <input type="hidden" name="auctionId" value="${auction.id}">

            <c:choose>
                <c:when test="${isFavorite}">
                    <input type="hidden" name="action" value="remove">
                    <button type="submit">Удалить из избранного</button>
                </c:when>

                <c:otherwise>
                    <input type="hidden" name="action" value="add">
                    <button type="submit">Добавить в избранное ♥</button>
                </c:otherwise>
            </c:choose>
        </form>
    </c:when>

    <c:otherwise>
        <p>
            <a href="${pageContext.request.contextPath}/login">
                Войдите, чтобы добавлять аукционы в избранное
            </a>
        </p>
    </c:otherwise>
</c:choose>

<hr>

<h2>Продавец</h2>

<p>
    <strong>Логин:</strong>
    <c:out value="${seller.username}"/>
</p>

<c:if test="${not empty contactUser}">

    <hr>

    <h2>Контакты: <c:out value="${contactRole}"/></h2>

    <p>
        <strong>Логин:</strong>
        <c:out value="${contactUser.username}"/>
    </p>

    <p>
        <strong>Email:</strong>
        <c:out value="${contactUser.email}"/>
    </p>

    <p>
        <strong>Телефон:</strong>
        <c:out value="${contactUser.phone}"/>
    </p>

</c:if>

<hr>

<a href="${pageContext.request.contextPath}/profile">
    Вернуться в личный кабинет
</a>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/photo-viewer.css">

<script src="${pageContext.request.contextPath}/js/auction-timer.js"></script>

<script src="${pageContext.request.contextPath}/js/photo-viewer.js"></script>


</body>
</html>
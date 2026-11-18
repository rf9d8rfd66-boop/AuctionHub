<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Личный кабинет — AuctionHub</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>
<body>

<h1>Личный кабинет</h1>



<nav>
    <a href="${pageContext.request.contextPath}/item/create">
        Создать аукцион
    </a>

    |

    <a href="${pageContext.request.contextPath}/catalog">
        Каталог аукционов
    </a>

    |

    <a href="${pageContext.request.contextPath}/my-items">
        Мои объявления
    </a>

    |

    <a href="${pageContext.request.contextPath}/my-bids">
        Мои ставки
    </a>

    |

    <a href="${pageContext.request.contextPath}/my-favorites">
        Избранное
    </a>

    <c:if test="${user.roleId == 2}">
        |
        <a href="${pageContext.request.contextPath}/admin">
            Панель администратора
        </a>
    </c:if>
</nav>

<hr>

<h2>Информация о пользователе</h2>

<p>
    <strong>ID:</strong>
    <c:out value="${user.id}"/>
</p>

<p>
    <strong>Логин:</strong>
    <c:out value="${user.username}"/>
</p>

<p>
    <strong>Email:</strong>
    <c:out value="${user.email}"/>
</p>

<p>
    <strong>Телефон:</strong>

    <c:choose>
        <c:when test="${not empty user.phone}">
            <c:out value="${user.phone}"/>
        </c:when>
        <c:otherwise>
            Не указан
        </c:otherwise>
    </c:choose>
</p>

<p>
    <strong>Дата регистрации:</strong>
    <c:out value="${user.createdAt}"/>
</p>

<hr>

<a href="${pageContext.request.contextPath}/logout">
    Выйти из аккаунта
</a>

</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ru">

<head>
    <meta charset="UTF-8">
    <title>Управление категориями — AuctionHub</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

      <link rel="stylesheet"
            href="${pageContext.request.contextPath}/css/admin.css">

</head>

<body>

<div class="container">

    <h1>Управление категориями</h1>

    <a href="${pageContext.request.contextPath}/admin">
        ← Назад в панель администратора
    </a>

    <c:if test="${not empty message}">
        <p class="${messageType}">
            <c:out value="${message}"/>
        </p>
    </c:if>

    <div class="card">

        <h2>Добавить категорию</h2>

        <form method="post"
              action="${pageContext.request.contextPath}/admin/categories">

            <input type="hidden" name="action" value="add">

            <input type="text"
                   name="name"
                   maxlength="100"
                   placeholder="Название новой категории"
                   required>

            <button type="submit" class="add-button">
                Добавить
            </button>

        </form>

    </div>

    <div class="card">

        <h2>Существующие категории</h2>

        <table>

            <thead>
            <tr>
                <th>ID</th>
                <th>Название</th>
                <th>Действие</th>
            </tr>
            </thead>

            <tbody>

            <c:forEach var="category" items="${categories}">

                <tr>

                    <td>
                        <c:out value="${category.id}"/>
                    </td>

                    <td>
                         <form method="post" action="${pageContext.request.contextPath}/admin/categories">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="categoryId" value="${category.id}">
                                <input type="text" name="name" value="<c:out value='${category.name}'/>" required maxlength="100">
                                <button type="submit">Сохранить</button>
                            </form>
                    </td>

                    <td>

                        <form method="post"
                              action="${pageContext.request.contextPath}/admin/categories"
                              data-confirm="Удалить эту категорию?">

                            <input type="hidden" name="action" value="delete">

                            <input type="hidden"
                                   name="categoryId"
                                   value="${category.id}">

                            <button type="submit" class="delete-button">
                                Удалить
                            </button>

                        </form>

                    </td>

                </tr>

            </c:forEach>

            </tbody>

        </table>

    </div>

</div>

<script src="${pageContext.request.contextPath}/js/form-confirm.js"></script>

</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:if test="${not empty error}">
    <p style="color: red;">
        <c:out value="${error}"/>
    </p>
</c:if>

<c:if test="${not empty success}">
    <p style="color: green;">
        <c:out value="${success}"/>
    </p>
</c:if>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Создание объявления</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>
<body>

<h1>Создание объявления</h1>

<form action="${pageContext.request.contextPath}/item/create"
      method="post"
      enctype="multipart/form-data">

    <div>
        <label for="title">Название товара:</label>
        <input type="text"
               id="title"
               name="title"
               maxlength="150"
               value="<c:out value='${title}'/>"
               required>
    </div>



    <div>
        <label for="categoryId">Категория:</label>

        <select id="categoryId" name="categoryId" required>
            <option value="">Выберите категорию</option>

            <c:forEach var="category" items="${categories}">
                <option value="${category.id}"
                    ${categoryId == category.id ? 'selected' : ''}>
                    <c:out value="${category.name}"/>
                </option>
            </c:forEach>

        </select>
    </div>



    <div>
        <label for="description">Описание:</label>
        <textarea id="description"
                  name="description"
                  rows="5"><c:out value="${description}"/></textarea>
    </div>



    <div>
        <label for="city">Город:</label>
        <input type="text"
               id="city"
               name="city"
               maxlength="100"
               value="<c:out value='${city}'/>"
               required>
    </div>



    <div>
        <label for="startingPrice">Начальная цена:</label>
        <input type="number"
               id="startingPrice"
               name="startingPrice"
               min="0.01"
               step="0.01"
               value="<c:out value='${startingPrice}'/>"
               required>
    </div>



    <div>
        <label for="bidStep">Шаг ставки:</label>
        <input type="number"
               id="bidStep"
               name="bidStep"
               min="0.01"
               step="0.01"
               value="<c:out value='${bidStep}'/>"
               required>
    </div>



    <div>
        <label for="images">Фотографии (максимум 5):</label>
        <input type="file"
               id="images"
               name="images"
               accept="image/*"
               multiple>
    </div>



    <button type="submit">Опубликовать объявление</button>

</form>

<a href="${pageContext.request.contextPath}/profile">
    Вернуться в личный кабинет
</a>

</body>
</html>
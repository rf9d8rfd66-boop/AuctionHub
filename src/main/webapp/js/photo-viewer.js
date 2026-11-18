document.addEventListener("DOMContentLoaded", function () {

    const modal = document.createElement("div");
    modal.className = "photo-modal";

    modal.innerHTML = `
        <button class="photo-modal-close" aria-label="Закрыть">&times;</button>
        <img class="photo-modal-image" src="" alt="Увеличенная фотография">
    `;

    document.body.appendChild(modal);

    const modalImage = modal.querySelector(".photo-modal-image");
    const closeButton = modal.querySelector(".photo-modal-close");

    // Открытие фотографии
    document.addEventListener("click", function (event) {

        const photo = event.target.closest(".auction-photo");

        if (!photo) {
            return;
        }

        modalImage.src = photo.src;
        modalImage.alt = photo.alt;

        modal.classList.add("active");
        document.body.style.overflow = "hidden";
    });

    // Закрытие фотографии
    function closeModal() {

        modal.classList.remove("active");
        modalImage.src = "";

        document.body.style.overflow = "";
    }

    closeButton.addEventListener("click", closeModal);

    modal.addEventListener("click", function (event) {

        if (event.target === modal) {
            closeModal();
        }

    });

    document.addEventListener("keydown", function (event) {

        if (event.key === "Escape") {
            closeModal();
        }

    });

});
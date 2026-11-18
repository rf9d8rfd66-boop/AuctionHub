document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("form[data-confirm]").forEach(form => {
        form.addEventListener("submit", function (event) {
            const message = form.dataset.confirm;

            if (!confirm(message)) {
                event.preventDefault();
            }
        });
    });
});
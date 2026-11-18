document.addEventListener("DOMContentLoaded", function () {

    const countdownElements = document.querySelectorAll(
        "#auction-countdown, .auction-countdown"
    );

    if (countdownElements.length === 0) {
        return;
    }

    let timerInterval;
    let reloadScheduled = false;

    function updateCountdowns() {

        let shouldReload = false;

        countdownElements.forEach(element => {

            const targetTime = Number(element.dataset.target);

            if (!Number.isFinite(targetTime)) {
                element.textContent = "Не удалось загрузить таймер";
                return;
            }

            const difference = targetTime - Date.now();

            if (difference <= 0) {

                element.textContent = "Время истекло";
                shouldReload = true;

                return;
            }

            const totalSeconds = Math.floor(difference / 1000);

            const days = Math.floor(totalSeconds / 86400);
            const hours = Math.floor((totalSeconds % 86400) / 3600);
            const minutes = Math.floor((totalSeconds % 3600) / 60);
            const seconds = totalSeconds % 60;

            const format = value => String(value).padStart(2, "0");

            let result = "";

            if (days > 0) {
                result += days + " д. ";
            }

            result +=
                format(hours) + ":" +
                format(minutes) + ":" +
                format(seconds);

            element.textContent = result;

        });

        if (shouldReload && !reloadScheduled) {

            reloadScheduled = true;

            clearInterval(timerInterval);

            setTimeout(function () {
                window.location.reload();
            }, 1000);

        }

    }

    updateCountdowns();

    timerInterval = setInterval(updateCountdowns, 1000);

});
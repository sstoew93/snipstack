document.querySelectorAll('.rating-form').forEach(form => {
    const stars = form.querySelectorAll('.star');
    const ratingValueInput = form.querySelector('#ratingValue');
    const hasVoted = form.getAttribute('data-has-voted') === 'true';

    if (hasVoted) {
        stars.forEach(star => star.classList.add('disabled'));
    } else {
        stars.forEach(star => {
            star.addEventListener('click', function () {
                ratingValueInput.value = this.getAttribute('data-value');
                form.submit();
            });
        });
    }
});

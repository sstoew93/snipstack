    document.addEventListener('DOMContentLoaded', () => {
    const toggleButtons = document.querySelectorAll('.toggle-snippet-button');

    toggleButtons.forEach((button) => {
    button.addEventListener('click', () => {

    const codeContainer = button.nextElementSibling;

    if (codeContainer.style.display === 'none' || codeContainer.style.display === '') {

    codeContainer.style.display = 'block';
    button.textContent = 'Hide Code';
} else {

    codeContainer.style.display = 'none';
    button.textContent = 'Show Code';
}
});
});
});
    document.addEventListener("DOMContentLoaded", function () {
    const profileDropdown = document.querySelector(".profile-dropdown");
    const dropdownMenu = document.querySelector(".dropdown-menu");

    profileDropdown.addEventListener("click", function (event) {
    event.stopPropagation(); // Prevent closing when clicking inside
    dropdownMenu.classList.toggle("show");
});

    // Hide dropdown when clicking outside
    document.addEventListener("click", function (event) {
    if (!profileDropdown.contains(event.target)) {
    dropdownMenu.classList.remove("show");
}
});
});
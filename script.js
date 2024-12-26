// Dummy product data (replace with actual data from backend)
const products = [
    { name: "MARVEL GO VN", price: 99, image: "baghome1.png" },
    { name: "BOO ESSENTIALS", price: 99, image: "baghome2.jpg" },
    { name: "LOTSO|BOO-STRAWBERRY FEST", price: 99, image: "baghome3.jpg" }
];

// Function to display products dynamically with a delay
function displayProducts(products) {
    const productContainer = document.getElementById("productContainer");
    productContainer.innerHTML = ""; // Clear existing products

    products.forEach((product, index) => {
        setTimeout(() => {
            const productDiv = document.createElement("div");
            productDiv.classList.add("product");

            const image = document.createElement("img");
            image.src = "sanphamimg/" + product.image;
            image.alt = product.name;

            const productName = document.createElement("h3");
            productName.textContent = product.name;

            const productPrice = document.createElement("p");
            productPrice.textContent = "SALE" + product.price.toFixed(2);

            productDiv.appendChild(image);
            productDiv.appendChild(productName);
            productDiv.appendChild(productPrice);

            productContainer.appendChild(productDiv);
        }, 300 * index);
    });
}

// Display products with a delay on page load
window.addEventListener("load", function() {
    displayProducts(products);
});

// Sticky header with smooth scrolling
window.addEventListener("scroll", function() {
    const header = document.querySelector("header");
    const banner = document.querySelector(".banner");

    if (window.scrollY > banner.offsetHeight) {
        header.classList.add("sticky");
        banner.style.marginTop = "70px";
    } else {
        header.classList.remove("sticky");
        banner.style.marginTop = "0";
    }
});

// Smooth scrolling for anchor links
document.querySelectorAll('a[href^=""]').forEach(anchor => {
    anchor.addEventListener("click", function(e) {
        e.preventDefault();

        const target = document.querySelector(this.getAttribute("href"));
        if (target) {
            window.scrollTo({
                top: target.offsetTop - 70,
                behavior: "smooth"
            });
        }
    });
});

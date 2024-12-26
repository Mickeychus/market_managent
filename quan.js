// Dummy product data (replace with actual data from backend)
const products = [
    { name: "QUẦN JEANS PARACHUTE CARGO STRAIGHT", price: 69, image: "jean1.jpg" },
    { name: "QUẦN JEANS NAM BAGGY SUÔNG", price: 99, image: "jean2.jpg" },
    { name: "QUẦN JEANS LOGO ESSENTIAL RELAX", price: 89, image: "jean3.jpg" },
    { name: "QUẦN KHAKI CÚC GẤU ESSENTIAL TAPE", price: 79, image: "kaki1.jpg" },
    { name: "QUẦN KHAKI JOGGER UN CÚC GẤU MF COOL ENTRY", price: 99, image: "kaki2.jpg" },
    { name: "QUẦN GIÓ PARACHUTE CAN PHỐI INF08", price: 99, image: "kaki3.jpg" },
    { name: "QUẦN NỈ LOGO INFINITEE 08 BOOLAAB JOGGER", price: 99, image: "jogger1.jpg" },
    { name: "QUẦN JOGGER UN BOOLIGAN ESSENTIALS WINTER", price: 39, image: "jogger2.jpg" },
    { name: "JOGGER REGULAR MARVEL COMICS BOOZILLA", price: 99, image: "jogger3.jpg" },
    { name: "QUẦN SHORT GIÓ NAM BERMUDA CAO CẤP BOOLAAB", price: 69, image: "short1.jpg" },
    { name: "QUẦN SHORT NỈ INTERLOCK UN IN LOGO LOONEY TUNES", price: 29, image: "short2.jpg" },
    { name: "QUẦN SHORT NAM SHORTS BERMUDA NỈ LOGO .", price: 59, image: "short3.jpg" }
];

// Function to display products dynamically with a delay
function displayProducts(productsquan) {
    const productContainer = document.getElementById("quanproductContainer");
    productContainer.innerHTML = ""; // Clear existing products

    products.forEach((product, index) => {
        setTimeout(() => {
            const productDiv = document.createElement("div");
            productDiv.classList.add("product");

            const image = document.createElement("img");
            image.src = "quan/" + product.image;
            image.alt = product.name;

            const productName = document.createElement("h3");
            productName.textContent = product.name;

            const productPrice = document.createElement("p");
            productPrice.textContent = "vnd" + product.price.toFixed(2);

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
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
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

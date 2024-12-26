// Dummy product data (replace with actual data from backend)
const products = [
    { name: "MŨ DAD CAP THÊU LOGO MARVEL GO VN", price: 39.99, image: "pk1.jpg" },
    { name: "BANDANA MARVEL GO VIETNAM BOOZILLA", price: 39.99, image: "pk2.jpg" },
    { name: "LONG SOCKS LOGO HARRY POTTER BOOZILLA", price: 39.99, image: "pk3.jpg" },
    { name: "PHỤ KIỆN COMBO 3 TẤT THẤP CỔ", price: 29.99, image: "pk4.jpg" },
    { name: "PHỤ KIỆN KEYCHAIN BOO IS BACK", price: 29.99, image: "pk5.jpg" },
    { name: "COMBO ĐỒ DÙNG HỌC TẬP BTS BOOLAAB", price: 29.99, image: "pk6.jpg" }
];

// Function to display products dynamically with a delay
function displayProducts(productsphukien) {
    const productContainer = document.getElementById("phukienproductContainer");
    productContainer.innerHTML = ""; // Clear existing products

    products.forEach((product, index) => {
        setTimeout(() => {
            const productDiv = document.createElement("div");
            productDiv.classList.add("product");

            const image = document.createElement("img");
            image.src = "phukien/" + product.image;
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

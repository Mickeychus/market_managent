// Dummy product data (replace with actual data from backend)
const products = [
    { name: "ÁO PHÔNG OVERSIZE ENTRY BOOLAAB", price: 529, image: "ao1.jpg" },
    { name: "ÁO PHÔNG GRAPHIC LOGO 02", price: 259, image: "ao2.jpg" },
    { name: "ÁO PHÔNG GRAPHIC GROWTH BOOLAAB", price: 299, image: "ao3.jpg" },
    { name: "ÁO PHÔNG GRAPHIC LẤY EM MỘT MIẾNG", price: 189, image: "ao4.jpg" },
    { name: "ÁO PHÔNG CAN PHỐI GROWTH BOOLAAB REGULAR", price: 169, image: "ao5.jpg" },
    { name: "ÁO PHÔNG BOXY GRAPHIC IN07 BOOLAAB", price: 199, image: "ao6.jpg" },
    { name: "ÁO PHÔNG GRAPHIC SÂNSITIVE", price: 369, image: "ao7.jpg" },
    { name: "ÁO PHÔNG GRAPHIC FEAR OF DOG", price: 399, image: "ao8.jpg" },
    { name: "ÁO PHÔNG OVERSIZED HNBXBLA", price: 399, image: "ao9.jpg" }
];

// Function to display products dynamically with a delay
function displayProducts(productsao) {
    const productContainer = document.getElementById("aoproductContainer");
    productContainer.innerHTML = ""; // Clear existing products

    products.forEach((product, index) => {
        setTimeout(() => {
            const productDiv = document.createElement("div");
            productDiv.classList.add("product");

            const image = document.createElement("img");
            image.src = "ao/" + product.image;
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

let img1 = document.getElementById("img1");
let img2 = document.getElementById("img2");

img1.addEventListener("click", (event) => {
    let lgImg = document.getElementsByClassName("main")[0];
    lgImg.src = img1.src;
    img1.className = "active";
    img2.className = "";
  });
  img2.addEventListener("click", (event) => {
    let lgImg = document.getElementsByClassName("main")[0];
    lgImg.src = img2.src;
    img1.className = "";
    img2.className = "active";
  });
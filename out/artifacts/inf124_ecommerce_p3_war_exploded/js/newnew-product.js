let orderForm = document.getElementById("odForm");
let oih3 = document.createElement("h3");
oih3.textContent = "Order Information";

document.title =
    "eCrocs | " +
    document.getElementsByClassName("product-right")[0].children[0].textContent;
orderForm.appendChild(oih3);
let form = document.createElement("form");
form.action = "./product";
form.method = "POST"
form.id = "orderForm"

form.innerHTML = `<ul>
  <li>
    <label for="qty">Quantity</label>
    <input type="number" id="qty" name="qty" value="1" min="1" required/>
  </li>
  <br /> <br />
    <button class= "cart" type="submit">Add To Cart</button>

</ul>`;
orderForm.appendChild(form);


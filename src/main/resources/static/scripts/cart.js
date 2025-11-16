window.onload = function () {
  let cart = JSON.parse(localStorage.getItem("cart")) || [];
  const cartItemsContainer = document.getElementById("cart-items");
  const cartTotal = document.getElementById("cart-total");

  // Hàm chuyển đổi giá từ string hoặc number sang number
  function parsePrice(price) {
    if (typeof price === "number") return price;
    if (typeof price === "string") {
      return Number(price.replace(/[^0-9]/g, "")) || 0;
    }
    return 0;
  }

  // Cập nhật hiển thị giỏ hàng
  function updateCartDisplay() {
    cartItemsContainer.innerHTML = "";
    let total = 0;

    // Kiểm tra giỏ hàng có sản phẩm không
    if (cart.length === 0) {
      cartItemsContainer.innerHTML = "<p>Giỏ hàng của bạn đang trống.</p>";
      cartTotal.innerText = "0đ";
      return;
    }

    cart.forEach((item, index) => {
      const itemPrice = parsePrice(item.price);
      const itemQty = item.quantity || 1;
      const itemTotal = itemPrice * itemQty;

      const itemElement = document.createElement("div");
      itemElement.classList.add("cart-item");

      itemElement.innerHTML = `
        <img src="${item.image}" alt="${item.name}" width="100">
        <div class="item-info">
          <h4>${item.name}</h4>
          <p>Đơn giá: ${itemPrice.toLocaleString()}đ</p>
          <label>Số lượng:
            <input type="number" min="1" value="${itemQty}" data-index="${index}" class="qty-input">
          </label>
          <p>Thành tiền: <span class="item-total">${itemTotal.toLocaleString()}đ</span></p>
          <button class="remove-btn" data-index="${index}">Xoá</button>
        </div>
      `;

      cartItemsContainer.appendChild(itemElement);
      total += itemTotal;
    });

    cartTotal.innerText = total.toLocaleString() + "đ";
  }

  // Thay đổi số lượng sản phẩm
  cartItemsContainer.addEventListener("input", function (e) {
    if (e.target.classList.contains("qty-input")) {
      const index = e.target.dataset.index;
      const newQty = parseInt(e.target.value);

      if (newQty > 0) { // Kiểm tra số lượng nhập vào có hợp lệ không
        cart[index].quantity = newQty;
        localStorage.setItem("cart", JSON.stringify(cart));
        updateCartDisplay();
      } else {
        e.target.value = cart[index].quantity; // Đặt lại giá trị khi nhập không hợp lệ
      }
    }
  });

  // Xoá sản phẩm
  cartItemsContainer.addEventListener("click", function (e) {
    if (e.target.classList.contains("remove-btn")) {
      const index = e.target.dataset.index;
      cart.splice(index, 1);
      localStorage.setItem("cart", JSON.stringify(cart));
      updateCartDisplay();
    }
  });

  // Nút "Tiếp tục mua sắm" quay về index.html
  document.getElementById("continue-shopping-btn").onclick = () => {
    // Khi nhấn "Tiếp tục mua sắm", lưu lại giỏ hàng hiện tại trong localStorage
    localStorage.setItem("cart", JSON.stringify(cart));
    window.location.href = "index.html"; // Quay về trang index.html
  };

  // Nút thanh toán (hiện tại chưa phát triển)
  document.getElementById("checkout-btn").onclick = () => {
    alert("Chức năng thanh toán đang được phát triển.");
  };

  // Cập nhật hiển thị giỏ hàng khi trang được tải
  updateCartDisplay();
};

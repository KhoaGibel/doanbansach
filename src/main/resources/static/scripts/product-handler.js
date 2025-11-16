// Lấy thông tin sản phẩm từ localStorage
const selectedProduct = JSON.parse(localStorage.getItem('selectedProduct'));

if (selectedProduct) {
  // Kiểm tra và hiển thị thông tin sản phẩm trên trang
  document.querySelector('.product-name').textContent = selectedProduct.name || 'Tên sản phẩm không có';
  document.querySelector('.product-image').src = selectedProduct.image || 'asset/img/default.jpg';
  document.querySelector('.product-price').textContent = selectedProduct.price || 'Giá đang cập nhật';
  document.querySelector('.product-description').textContent = selectedProduct.description || 'Mô tả sản phẩm đang cập nhật...';
} else {
  // Nếu không có sản phẩm được chọn, hiển thị thông báo
  alert('No products selected');
  window.location.href = 'index.html'; // Chuyển hướng về trang chính nếu không tìm thấy sản phẩm
}

document.addEventListener('DOMContentLoaded', function () {
  const productItems = document.querySelectorAll('.product-item');

  productItems.forEach(item => {
    const priceElement = item.querySelector('.price');
    const discountLabel = item.querySelector('.discount-label');

    if (priceElement && discountLabel) {
      const newPrice = parseInt(priceElement.dataset.price);
      const oldPrice = parseInt(priceElement.dataset['oldPrice']);

      if (!isNaN(newPrice) && !isNaN(oldPrice) && oldPrice > newPrice) {
        const discount = Math.round(((oldPrice - newPrice) / oldPrice) * 100);
        discountLabel.textContent = `-${discount}%`;
      } else {
        discountLabel.textContent = ''; // Không có giảm giá
      }
    }
  });
});

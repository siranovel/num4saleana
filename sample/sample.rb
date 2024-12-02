require 'num4saleana'

sales_info = [
    {"name" => "商品1", "stock" => 2380, "sale" => 24000, "quantity" => 100},
    {"name" => "商品2", "stock" => 2380, "sale" => 12000, "quantity" => 100},
    {"name" => "商品3", "stock" => 2380, "sale" => 10000, "quantity" => 100},
    {"name" => "商品4", "stock" => 2380, "sale" => 6000,  "quantity" => 100},
    {"name" => "商品5", "stock" => 2380, "sale" => 2700,  "quantity" => 100},
]
sale = Num4SaleAnaLib::SalesAnaLib.new
sale.crossabcana(sales_info)



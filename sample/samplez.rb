require 'num4saleana'
require_relative('mymatcher')

RSpec.describe Num4SaleAnaLib do
    describe Num4SaleAnaLib::SalesAnaLib do
        let!(:sale) { Num4SaleAnaLib::SalesAnaLib.new }
        it '#abcana' do
            sales_info = [
                {"name" => "商品1", "revenue" => 2400000},
                {"name" => "商品2", "revenue" => 1200000},
                {"name" => "商品3", "revenue" => 1000000},
                {"name" => "商品4", "revenue" => 600000},
                {"name" => "商品5", "revenue" => 270000},
            ]
            expect(
                sale.abcana(sales_info)
            ).to is_exist("abcana.jpeg")
        end
    end
end

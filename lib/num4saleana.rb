require 'java'
require 'num4saleana.jar'
require 'commons-math3-3.6.1.jar'
require 'jfreechart-1.5.4.jar'

java_import 'SalesAna'
java_import 'java.util.HashMap'
java_import 'java.util.ArrayList'
# 販売分析
#  (Apache commoms math3使用)
module Num4SaleAnaLib
    # 売上分析
    #  (グラフは、jfreechart ver1.5を使用)
    class SalesAnaLib
        def initialize
            @sales = SalesAna.getInstance()
        end
        # ABC分析
        #
        # @overload abcana(sales_info)
        #   @param [hash] sales_info 売上データ(name:商品名 revenue:売上金額)
        #   @return [void] abcana.jpegファイルを出力
        # @example
        #   sales_info = [
        #        {"name" => "商品1", "revenue" => 2400000},
        #        {"name" => "商品2", "revenue" => 1200000},
        #        {"name" => "商品3", "revenue" => 1000000},
        #        {"name" => "商品4", "revenue" => 600000},
        #        {"name" => "商品5", "revenue" => 270000},
        #   ]
        #   sale = Num4SaleAnaLib::SalesAnaLib.new
        #   sale.abcana(sales_info)
        #   => abcana.jpeg
        # @note
        #   グラフは、jfreechartを使用
        def abcana(sales_info)
            list = ArrayList.new
            sales_info.each{|val|
                map = HashMap.new
                val.each{|k,v| map[k] = v}
                list.add(map)
            }
            @sales.abcAna(list)
        end
        # クロスABC分析
        #
        # @overload crossabcana(sales_info)
        #   @param [hash] sales_info 売上データ(name:商品名 stock:仕入額 sale:販売額 quantity:数量)
        #   @return [void] jframe出力
        # @example
        #   sales_info = [
        #    {"name" => "商品1", "stock" => 2380, "sale" => 24000, "quantity" => 100},
        #    {"name" => "商品2", "stock" => 2380, "sale" => 12000, "quantity" => 100},
        #    {"name" => "商品3", "stock" => 2380, "sale" => 10000, "quantity" => 100},
        #    {"name" => "商品4", "stock" => 2380, "sale" => 6000,  "quantity" => 100},
        #    {"name" => "商品5", "stock" => 2380, "sale" => 2700,  "quantity" => 100},
        #   ]
        #   sale = Num4SaleAnaLib::SalesAnaLib.new
        #   sale.crossabcana(sales_info)
        #   => 画面表示
        def crossabcana(sales_info)
            list = ArrayList.new
            sales_info.each{|val|
                map = HashMap.new
                val.each{|k,v| map[k] = v}
                list.add(map)
            }
            @sales.crossAbcAna(list)
        end

    end
end


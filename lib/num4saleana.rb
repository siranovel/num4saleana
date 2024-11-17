require 'java'
require 'num4saleana.jar'
require 'commons-math3-3.6.1.jar'
require 'jfreechart-1.5.4.jar'

java_import 'SalesAna'
java_import 'java.util.HashMap'
java_import 'java.util.ArrayList'
# 販売分析
#  (Apache commoms math3使用)
#  (グラフは、jfreechart ver1.5を使用)
module Num4SaleAnaLib
    # 売上分析
    class SalesAnaLib
        def initialize
            @sales = SalesAna.getInstance()
        end
        def abcana(sales_info)
            list = ArrayList.new
            sales_info.each{|val|
                map = HashMap.new
                val.each{|k,v|
                    map[k] = v
                }
                list.add(map)
            }
            @sales.abcAna(list)
        end
    end
end


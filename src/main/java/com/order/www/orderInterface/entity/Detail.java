package com.order.www.orderInterface.entity;

import java.util.List;

public class Detail {

    /**
     * Status : 0
     * Msg : 成功
     * Result : [{"ItemCode":"123","Num":10,"Detail":{"Order":5,"Product":2,"Collocation":1,"Promotion":2,"ProductSKU":0,"PromotionSKU":0,"ColloPromotion":0}}]
     */

    private int Status;
    private String Msg;
    private List<ResultBean> Result;

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    public List<ResultBean> getResult() {
        return Result;
    }

    public void setResult(List<ResultBean> Result) {
        this.Result = Result;
    }

    public static class ResultBean {
        /**
         * ItemCode : 123
         * Num : 10
         * Detail : {"Order":5,"Product":2,"Collocation":1,"Promotion":2,"ProductSKU":0,"PromotionSKU":0,"ColloPromotion":0}
         */

        private String ItemCode;
        private int Num;
        private DetailBean Detail;

        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String ItemCode) {
            this.ItemCode = ItemCode;
        }

        public int getNum() {
            return Num;
        }

        public void setNum(int Num) {
            this.Num = Num;
        }

        public DetailBean getDetail() {
            return Detail;
        }

        public void setDetail(DetailBean Detail) {
            this.Detail = Detail;
        }

        public static class DetailBean {
            /**
             * Order : 5
             * Product : 2
             * Collocation : 1
             * Promotion : 2
             * ProductSKU : 0
             * PromotionSKU : 0
             * ColloPromotion : 0
             */

            private int Order;
            private int Product;
            private int Collocation;
            private int Promotion;
            private int ProductSKU;
            private int PromotionSKU;
            private int ColloPromotion;

            public int getOrder() {
                return Order;
            }

            public void setOrder(int Order) {
                this.Order = Order;
            }

            public int getProduct() {
                return Product;
            }

            public void setProduct(int Product) {
                this.Product = Product;
            }

            public int getCollocation() {
                return Collocation;
            }

            public void setCollocation(int Collocation) {
                this.Collocation = Collocation;
            }

            public int getPromotion() {
                return Promotion;
            }

            public void setPromotion(int Promotion) {
                this.Promotion = Promotion;
            }

            public int getProductSKU() {
                return ProductSKU;
            }

            public void setProductSKU(int ProductSKU) {
                this.ProductSKU = ProductSKU;
            }

            public int getPromotionSKU() {
                return PromotionSKU;
            }

            public void setPromotionSKU(int PromotionSKU) {
                this.PromotionSKU = PromotionSKU;
            }

            public int getColloPromotion() {
                return ColloPromotion;
            }

            public void setColloPromotion(int ColloPromotion) {
                this.ColloPromotion = ColloPromotion;
            }
        }
    }
}

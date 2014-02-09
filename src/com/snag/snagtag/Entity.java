package com.snag.snagtag;


import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;


public class Entity extends GenericJson {

    @Key("_id")
    private String id; 
    @Key
    private String Store;
    @Key
    private String ImageUrl;
    @Key
    private String PurchaseUrl;
    @Key
    private String Price;
    @Key
    private String Date;
    @Key  
    private String Type;
    @Key
    private String Title;
    @Key
    private int	tagCount;
    
        public Entity(){}
        
        public Entity(String id) {
                this.id = id;
        }
        
        public Entity(String id, 
                        String Store, 
                        String ImageUrl,
                        String PurchaseUrl,
                        String Price,
                        String Date,
                        String Type,
                        String Title){
                this.id = id;
                this.Store= Store;
                this.ImageUrl = ImageUrl;
                this.PurchaseUrl= PurchaseUrl;
                this.Price = Price;
                this.Date = Date;
                this.Type = Type;
                this.Title = Title;
                this.tagCount = 0;
        }
        
        public String getId() {
                return id;
        }
        public void setId(String id) {
                this.id = id;
        }

		public String getStore() {
			return Store;
		}

		public void setStore(String store) {
			Store = store;
		}
		
		public String getTitle() {
			return Title;
		}

		public void setTitle(String title) {
			Title = title;
		}
		public String getImageUrl() {
			return ImageUrl;
		}

		public void setImageUrl(String imageUrl) {
			ImageUrl = imageUrl;
		}

		public String getPurchaseUrl() {
			return PurchaseUrl;
		}

		public void setPurchaseUrl(String purchaseUrl) {
			PurchaseUrl = purchaseUrl;
		}

		public String getPrice() {
			return Price;
		}

		public void setPrice(String price) {
			Price = price;
		}

		public String getDate() {
			return Date;
		}

		public void setDate(String date) {
			Date = date;
		}

		public String getType() {
			return Type;
		}

		public void setType(String type) {
			Type = type;
		}
 
        
}
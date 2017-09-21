package hk.xhy.mate7utils.model;

/**
 * Created by xuhaoyang on 2017/4/23.
 */

public class Item extends Model {

    private int id;
    private String realname;
    private String showname;
    private int sort;
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Item() {
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getShowname() {
        return showname;
    }

    public void setShowname(String showname) {
        this.showname = showname;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Item(int id, String realname, int sort) {
        this.id = id;
        this.realname = realname;
        this.sort = sort;
    }

    /**
     * 转换json为Model
     *
     * @param json
     * @return
     */
    public static Item parseObject(final String json) {
        return Model.parseObject(json, Item.class);
    }
}

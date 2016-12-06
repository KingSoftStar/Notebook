package com.kingsoftstar.notebook;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by KingSoftStar on 2016/12/1.
 */

class Note implements Serializable, Comparable {

    private String mTitle;
    private String mEditTime;
    private String mContent;
    private boolean mIsEncryption;
    private String mIdentify;
    private String mCreateTime;
    private String mPassword;

    Note() {
        SimpleDateFormat createFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat identifyFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date date = new Date();
        Random random = new Random(date.getTime());
        mCreateTime = createFormat.format(date);
        mIdentify = String.format("%s%8d", identifyFormat.format(date), random.nextInt());
        mIsEncryption = false;
        mTitle = "";
        mEditTime = mCreateTime;
        mContent = "";
        mPassword = "";
    }

    /**
     * @param identify   标识
     * @param title      标题
     * @param createTime 创建时间
     * @param editTime   最后编辑时间
     * @param content    内容
     * @param encryption 加密标识符
     * @param password   加密密码
     */
    Note(String identify, String title, String createTime, String editTime, String content, boolean encryption, String password) {
        mIdentify = identify;
        mTitle = title;
        mCreateTime = createTime;
        mEditTime = editTime;
        mContent = content;
        mIsEncryption = encryption;
        mPassword = password;
    }

    /**
     * @param identify   标识
     * @param title      标题
     * @param createTime 创建时间
     * @param editTime   最后编辑时间
     * @param content    内容
     */
    Note(String identify, String title, String createTime, String editTime, String content) {
        mIdentify = identify;
        mTitle = title;
        mCreateTime = createTime;
        mEditTime = editTime;
        mContent = content;
        mIsEncryption = false;
        mPassword = "";
    }

    String getIdentify() {
        return mIdentify;
    }

    String getTitle() {
        return mTitle;
    }

    void setTitle(String title) {
        mTitle = title;
    }

    String getCreateTime() {
        return mCreateTime;
    }

    String getEditTime() {
        return mEditTime;
    }

    String getSimpleContent() {
        if (mIsEncryption) {
            return "/*已加密*/";
        } else {
            String ret = mContent.trim();
            if (ret.length() > 100) {
                ret = ret.substring(0, 100);
            }
            return ret;
        }
    }

    void setPassword(String password) {
        if (mPassword.isEmpty()) {
            mIsEncryption = true;
            mPassword = password;
            mContent = getEncryption(mContent);
        } else {
            mContent = getEncryption(mContent, password);
            mPassword = password;
        }
    }

    /**
     * 变更加密开关，使用用户密码（全局）对笔记内容进行加密
     *
     * @param to_Encryption true--加密存储笔记内容；false--解密存储笔记内容
     */
    void setEncryption(boolean to_Encryption) {
        if (to_Encryption) {
            if (!mIsEncryption) {
                mContent = getEncryption(mContent);
            }
        }
    }

    String getContent() {
        return mContent;
    }

    void setContent(String content) {
        if (mIsEncryption) {
            mContent = getEncryption(content);
        } else {
            mContent = content;
        }
        SimpleDateFormat editFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mEditTime = editFormat.format(new Date());
    }

    String getContent(String password) throws Exception {
        if (mIsEncryption) {
            if (password.equals(mPassword)) {
                return getDecryption(mContent);
            } else {
                return "/*已加密*/";
            }
        } else {
            return mContent;
        }
    }

    String getEncryption(String beEncryption) {
//        todo: 返回加密后的字符串
        return "";
    }

    String getDecryption(String beDecryption) {
//        todo: 返回解密后的字符串
        return "";
    }

    String getEncryption(String beDecryption, String password) {
        // todo: 用原密码解密字符串
        // TODO: 2016/12/2 用新密码加密字符串并返回
        return "";
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <p>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Object o) {
        if (o.getClass().getName().equals(this.getClass().getName())) {
            Note note = (Note) o;
            return mIdentify.compareTo(note.getIdentify());
        }
        throw new ClassCastException("变量类型错误");
    }
}

package com.ityunqi.web.servlet;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ityunqi.pojo.*;
import com.ityunqi.pojo.Order.Order;
import com.ityunqi.pojo.Order.OrderDetail;
import com.ityunqi.pojo.shopcart.*;
import com.ityunqi.service.ShopcartService;
import com.ityunqi.service.impl.ShopcartServiceImpl;
import com.ityunqi.util.SqlSessionFactoryUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/shopcart/*")
public class ShopcartServlet extends BaseServlet {

    private ShopcartService shopcartService = new ShopcartServiceImpl();

    /**
     * 一个封装的获取cookie的私有方法（私有方法封装性好，更安全）（私有方法只能在当前类使用，满足需求）
     * @param req
     * @param resp
     * @return
     */
    private int getUserid(HttpServletRequest req,HttpServletResponse resp) {
        //1. 获取Cookie数组
        Cookie[] cookies = req.getCookies();
        // 准备接收cookie中的用户id
        String value = null;
        //2. 遍历数组
        for (Cookie cookie : cookies) {
            //3. 获取数据
            String name = cookie.getName();
            System.out.println(name);
            if ("userid".equals(name)) {
                value = cookie.getValue();
                System.out.println(value);
                break;
            }
        }
        if (value != null) {
            // 转换类型，获取int类型的userid
            int userid = Integer.parseInt(value);
            return userid;
        }
        return -1;
    }

    /**
     * 查询购物车所有
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void selectAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //===================一、获取cookie=====================
        int userid = getUserid(req,resp);
        System.out.println("成功获取cookie！！！"+userid);
        if (userid != -1) {
            //===================二、调用方法查询所有=====================
            List<ShopcartBean> shopcarts = shopcartService.selectAll(userid);
            //===================三、响应数据=====================
            String jsonString = JSON.toJSONString(Result.success(shopcarts));
            System.out.println(jsonString);
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write(jsonString);
        }else {
            System.out.println("获取cookie失败");
        }
    }

    /**
     * 查询回显
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void selectByid(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        BufferedReader br = req.getReader();
        String params = br.readLine();
        // 解析JSON字符串为JSON对象
        Map<String, String> jsonData = JSONObject.parseObject(params, new TypeReference<Map<String,String>>() {});
        // 获取键值对
        String _id = jsonData.get("id");
        System.out.println(_id);
        int id = Integer.parseInt(_id);

        session.setAttribute("milkteaid",_id);
        System.out.println("已经将id添加到了session中");


        //===================二、调用方法查询所有=====================
        AddShopcartBean addShopcartBean = shopcartService.selectByid(id);
        //===================三、响应数据===========================
        String jsonString = JSON.toJSONString(Result.success(addShopcartBean));
        System.out.println(jsonString);
        resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().write(jsonString);
    }
    /**
     * 增加购物车奶茶数据
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //===================一、接收前端数据=====================
        //1. 接收前端传递的json对象
        BufferedReader br = req.getReader();
        String params = br.readLine();//json字符串
        System.out.println(params);
        //2. 转为ShopcartDetailBean对象
        Map<String, String> jsonData = JSONObject.parseObject(params, new TypeReference<Map<String,String>>() {});
        System.out.println("已经将json转换为了map集合: "+jsonData);
        String count0 = jsonData.get("count");
        int count1 = Integer.parseInt(count0);

        //从session中获取id
        HttpSession session = req.getSession();
        String milkteaid1 = (String) session.getAttribute("milkteaid");
        int milkteaid2 = Integer.parseInt(milkteaid1);

        System.out.println("从session中获取的id为: "+milkteaid2);


        ShopcartDetailBean shopcartDetailBean = new ShopcartDetailBean(milkteaid2,count1);

        //===================二、获取cookie=====================
        int userid = getUserid(req,resp);
        if (userid != -1) {
        //int userid = 1;

            //===================三、判断购物车中是否已有该奶茶id，并执行对应操作=====================
            //1. 调用方法获取购物车中奶茶id数组
            int[] ids = shopcartService.selectids(userid);
            //2. 根据ids数组判断添加的奶茶是否已存在
            int milkteaid = shopcartDetailBean.getMilkteaid();
            int count = shopcartDetailBean.getCount();
            //2.1 数组为空，调用增加方法
            if (ids.length == 0) {
                ShopcartDetail shopcartDetail = new ShopcartDetail(userid, milkteaid, count);
                shopcartService.add(shopcartDetail);
            } else {
                boolean research = false;
                for (int id : ids) {
                    //2.2 数组不为空，已有该奶茶，调用修改方法
                    if (id == milkteaid) {
                        shopcartService.updateAdd(milkteaid, count, userid);
                        research = true;
                        break;
                    }
                }
                //2.3 数组不为空，没有该奶茶，调用增加方法
                if (research == false) {
                    //如果该奶茶id不存在，则执行增加操作
                    ShopcartDetail shopcartDetail = new ShopcartDetail(userid, milkteaid, count);
                    shopcartService.add(shopcartDetail);
                }
            }

            //===================四、响应数据=====================
            String jsonString = JSON.toJSONString(Result.success());
            System.out.println("执行成功");
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write(jsonString);
        }
    }

    /**
     * 修改购物车奶茶数据
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void update(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //===================一、接收前端数据(json)=====================
        BufferedReader br = req.getReader();
        String params = br.readLine();

        System.out.println("前端传的数据:"+params);

        // 解析JSON字符串为JSON对象
        Map<String, String> jsonData = JSONObject.parseObject(params, new TypeReference<Map<String,String>>() {});

        // 获取键值对
        String _milkteaid = jsonData.get("id");
        String _count = jsonData.get("count");
        int milkteaid = Integer.parseInt(_milkteaid);
        int count = Integer.parseInt(_count);

        //===================二、获取cookie=====================
        int userid = getUserid(req,resp);
        if (userid != -1) {
        //    int userid = 1;

            //===================三、调用修改方法=====================
            shopcartService.update(milkteaid, count, userid);
        System.out.println("成功执行方法");

        //===================四、响应数据=====================
        String jsonString = JSON.toJSONString(Result.success(milkteaid));
        resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().write(jsonString);
        System.out.println("成功响应数据");
        }
    }

    /**
     * 删除购物车奶茶数据
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //===================一、接收前端数据=====================
        String _milkteaid = req.getParameter("id");
        int milkteaid = Integer.parseInt(_milkteaid);

        //===================二、获取cookie=====================
        //int userid = getUserid(req,resp);
        //if (userid != -1) {
        int userid = 1;

            //===================三、调用删除方法=====================
            shopcartService.delete(milkteaid, userid);

            //===================四、响应数据=====================
            String jsonString = JSON.toJSONString(Result.success());
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write(jsonString);
        //}
    }

    /**
     * 返回购物车总金额
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void totalPrice(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //===================一、获取cookie=====================
        int userid = getUserid(req,resp);
        if (userid != -1) {
        //int userid = 1;

            //===================二、获取购物车总金额=====================
            //1. 查询购物车中的商品信息
            List<ShopcartDetailBean2> shopcartDetailBean2s = shopcartService.selectAllBysd(userid);
            //2. 计算订单总金额
            int totalPrice = 0;
            for (ShopcartDetailBean2 shopcartDetailBean2 : shopcartDetailBean2s) {
                totalPrice += (shopcartDetailBean2.getPrice() * shopcartDetailBean2.getCount());
            }

            //===================三、响应数据=====================
            String jsonString = JSON.toJSONString(Result.success(totalPrice));
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write(jsonString);
        }
    }

    /**
     * 结算购物车
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void checkout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //结算步骤安全性要求高，且涉及对多个数据库的操作，为了避免发生异常，导致数据的不一致性，此处的业务逻辑使用事务来管理（ABCDEF）

        //A、创建SqlSessionFactory工厂类对象
        SqlSessionFactory factory = SqlSessionFactoryUtils.getSqlSessionFactory();
        SqlSession sqlSession = null;
        try {
            //B、获取SqlSession对象来管理事务
            sqlSession = factory.openSession();
            //C、开启事务
            sqlSession.getConnection().setAutoCommit(false);

            //===================一、获取cookie=====================
            int userid = getUserid(req,resp);
            if (userid != -1) {

                //===================二、执行业务逻辑=====================
                //1. 查询购物车中的商品信息
                List<ShopcartDetailBean2> shopcartDetailBean2s = shopcartService.selectAllBysd(userid);
                //2. 计算订单总金额
                int totalPrice = 0;
                for (ShopcartDetailBean2 shopcartDetailBean2 : shopcartDetailBean2s) {
                    totalPrice += (shopcartDetailBean2.getPrice() * shopcartDetailBean2.getCount());
                }
                //3. 创建订单
                Order order = new Order(null, userid, totalPrice, 0);
                shopcartService.insertOrder(order);//将订单增加到订单数据库
                int orderid = shopcartService.getOrderid();//获取新自增的订单id
                //4. 将购物车中的商品信息转移到订单详情表中
                for (ShopcartDetailBean2 shopcartDetailBean2 : shopcartDetailBean2s) {
                    OrderDetail OrderDetail = new OrderDetail(orderid, shopcartDetailBean2.getId(), shopcartDetailBean2.getCount());
                    shopcartService.insertOrderDetail(OrderDetail);
                }
                //5. 修改对应奶茶销量
                for (ShopcartDetailBean2 shopcartDetailBean2 : shopcartDetailBean2s) {
                    shopcartService.updateBymt(shopcartDetailBean2.getCount(), shopcartDetailBean2.getId());
                }
                //6. 删除购物车中该用户的商品信息
                shopcartService.deleteByUserid(userid);

                //===================三、响应数据=====================
                String jsonString = JSON.toJSONString(Result.success());
                resp.setContentType("application/json;charset=utf-8");
                resp.getWriter().write(jsonString);
            }

            //D、提交事务
            sqlSession.commit();

        } catch (Exception e) {
            //E、发生异常时回滚事务
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            e.printStackTrace();
        } finally {
            //F、关闭SqlSession
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

}
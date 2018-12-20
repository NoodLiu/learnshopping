package com.neuedu.service.impl;

import ch.qos.logback.core.util.FileUtil;
import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.MonitorHeartbeatSynResponse;
import com.alipay.demo.trade.DemoHbRunner;
import com.alipay.demo.trade.Main;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.*;
import com.alipay.demo.trade.model.hb.*;
import com.alipay.demo.trade.model.result.AlipayF2FPayResult;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.model.result.AlipayF2FRefundResult;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.alipay.demo.trade.utils.Utils;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.neuedu.common.Const;
import com.neuedu.common.ProductStatusEnum;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.*;
import com.neuedu.entity.*;
import com.neuedu.entity.Product;
import com.neuedu.service.OrderService;
import com.neuedu.util.BigdecimalUtils;
import com.neuedu.util.DateUtils;
import com.neuedu.util.OrderIdUtils;
import com.neuedu.util.PropertiesUtils;
import com.neuedu.vo.CarOrderItemVo;
import com.neuedu.vo.OrderItemVo;
import com.neuedu.vo.OrderVo;
import com.neuedu.vo.ShippingVo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ShippingMapper shippingMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;

    /**
     * 创建-----订单
     *
     * @param userId
     * @param shippingId
     * @return
     */
    @Override
    public ServerResponse create(Integer userId, Integer shippingId) {
        /* step1 参数校验 */
        if (shippingId == null) {
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
        /* step2 根据userId查询已选中的商品 */
        List<Cart> cartList = cartMapper.selectIsCheckProduct(userId);
        /* step3 List<Cart>----> List<OrderItem>*/
        ServerResponse serverResponse = getCartOrderItem(userId, cartList);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        /* step4 创建订单保存到数据库中 */
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        if (orderItemList == null || orderItemList.size() < 0) {
            return ServerResponse.createServerResponseByError("购物车为空");
        }
        /* 计算订单总价格 */
        BigDecimal bigDecimal = sumMoney(orderItemList);

        Order order = createOrder(userId, shippingId, bigDecimal);
        /* step5 将Order 保存到数据库中 */
        if (order == null) {
            return ServerResponse.createServerResponseByError("订单创建失败");
        }
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }
        /* 批量插入 */
        int result = orderItemMapper.insertBacth(orderItemList);
        if (result < 0) {
            return ServerResponse.createServerResponseByError("数据插入失败");
        }
        /* step6 扣库存 */
        reduceProductStock(orderItemList);
        /* step7 清空已下单的商品 */
        clearCart(cartList);
        /* step8 返回orderVo */
        OrderVo orderVo = createOrderVo(order, orderItemList, shippingId);

        return ServerResponse.createServerResponseBySuccess(orderVo);
    }

    /**
     * 取消订单
     *
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse cancel(Integer userId, Long orderNo) {
        /* 非空校验 */
        if (orderNo == null) {
            return ServerResponse.createServerResponseByError("订单不存在");
        }
        /* 查询订单 */
        Order order = orderMapper.selectByuserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createServerResponseByError("订单不存在");
        }
        /* step3 判断订单状态并取消 */
        if (order.getStatus() != Const.OrderStaEnum.ORDER_UN_PAY.getCode()) {
            return ServerResponse.createServerResponseByError("订单不可取消");
        }
        order.setStatus(Const.OrderStaEnum.ORDER_CANCELED.getCode());
        int result = orderMapper.updateByPrimaryKey(order);
        if (result > 0) {
            return ServerResponse.createServerResponseBySuccess("取消成功");
        }
        /* step4 返回结果*/
        return ServerResponse.createServerResponseByError("取消失败");
    }

    /**
     * 获取购物车中商品
     *
     * @param userId
     * @return
     */
    @Override
    public ServerResponse get_order_cart_product(Integer userId) {
        /* step1 查找购物车 */
        List<Cart> carts = cartMapper.selectIsCheckProduct(userId);
        /* step2 List<Cart>--> list<OrderItem> */
        ServerResponse serverResponse = new ServerResponse();
        if (carts != null) {
            serverResponse = getCartOrderItem(userId, carts);
        }
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        /* step3 组装Vo */
        CarOrderItemVo carOrderItemVo = new CarOrderItemVo();
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        if (orderItemList == null || orderItemList.size() <= 0) {
            return ServerResponse.createServerResponseByError("购物车为空");
        }
        for (OrderItem orderItem : orderItemList) {
            orderItemVoList.add(toOrderItemVo(orderItem));
        }
        carOrderItemVo.setOrderItemVoList(orderItemVoList);
        carOrderItemVo.setImageHost(PropertiesUtils.readKey("imageHost"));
        carOrderItemVo.setTotalPrice(sumMoney(orderItemList));
        /* step4 返回结果 */
        return ServerResponse.createServerResponseBySuccess(carOrderItemVo);
    }

    /**
     * 订单列表
     *
     * @param userId
     * @return
     */
    @Override
    public ServerResponse list(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = Lists.newArrayList();
        if (userId == null) {
            //查询所有
            orders = orderMapper.selectAll();
        } else {
            //查询当前用户
            orders = orderMapper.selectOrderByUserId(userId);
        }
        List<OrderVo> orderVos = Lists.newArrayList();
        for (Order order : orders) {
            List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
            OrderVo orderVo = createOrderVo(order, orderItemList, order.getShippingId());
            orderVos.add(orderVo);
        }
        PageInfo pageInfo = new PageInfo(orderVos);
        return ServerResponse.createServerResponseBySuccess(pageInfo);
    }

    @Override
    public ServerResponse detail(Long orderNo) {
        /* 参数校验 */
        if (orderNo == null) {
            return ServerResponse.createServerResponseByError("参数为空");
        }
        /* 查询订单 */
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createServerResponseByError("订单不存在");
        }
        /* 获取orderVo */
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
        OrderVo orderVo = createOrderVo(order, orderItemList, order.getShippingId());
        return ServerResponse.createServerResponseBySuccess(orderVo);
    }

    /**
     * 创建OrderVo
     *
     * @param order
     * @param orderItems
     * @param shippingId
     * @return
     */
    private OrderVo createOrderVo(Order order, List<OrderItem> orderItems, Integer shippingId) {
        OrderVo orderVo = new OrderVo();
        System.out.println();
        /* 拼接OrderItemVO */
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItems) {
            OrderItemVo orderItemVo = toOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);

        orderVo.setImageHost(PropertiesUtils.readKey("imageHost"));
        /* 获取地址 */
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        if (shipping != null) {
            System.out.println();
            ShippingVo shippingVo = toShippingVo(shipping);
            orderVo.setShippingVo(shippingVo);
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingId(shippingId);
        }
        orderVo.setStatus(order.getStatus());
        Const.OrderStaEnum staEnum = Const.OrderStaEnum.getmsg(order.getStatus());
        if (staEnum != null) {
            orderVo.setStatusDesc(staEnum.getMsg());
        }
        orderVo.setPostage(0);
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        Const.PayType payType = Const.PayType.getmsg(order.getPaymentType());
        if (payType != null) {
            orderVo.setPaymentTypeDesc(payType.getMsg());
        }
        orderVo.setOrderNo(order.getOrderNo());
        return orderVo;
    }

    /**
     * Shipping -->> ShippingVo
     *
     * @param shipping
     * @return
     */
    private ShippingVo toShippingVo(Shipping shipping) {
        ShippingVo shippingVo = new ShippingVo();
        if (shipping != null) {
            shippingVo.setRevricerDistrict(shipping.getRevricerDistrict());
            shippingVo.setRecriverCity(shipping.getRecriverCity());
            shippingVo.setReceiverZip(shipping.getReceiverZip());
            shippingVo.setReceiverProvince(shipping.getReceiverProvince());
            shippingVo.setReceiverPhone(shipping.getReceiverPhone());
            shippingVo.setReceiverName(shipping.getReceiverName());
            shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        }
        return shippingVo;
    }

    /**
     * 转OrderItemVo
     */
    private OrderItemVo toOrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();
        if (orderItem != null) {
            orderItemVo.setQuantity(orderItem.getQuantity());
            orderItemVo.setOrderNo(orderItem.getOrderNo());
            orderItemVo.setCreateTime(DateUtils.dateToStr(orderItem.getCreateTime()));
            orderItemVo.setProductId(orderItem.getProductId());
            orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
            orderItemVo.setProductName(orderItem.getProductName());
            orderItemVo.setTotalPrice(orderItem.getTotalPrice());
            orderItemVo.setProductImage(orderItem.getProductImage());
        }
        return orderItemVo;
    }

    /**
     * 清空购物车中已下单的商品
     */
    private void clearCart(List<Cart> cartList) {
        if (cartList != null && cartList.size() > 0) {
            cartMapper.deleteCart(cartList);
        }
    }

    /**
     * 扣库存
     */
    private void reduceProductStock(List<OrderItem> orderItems) {
        if (orderItems != null || orderItems.size() > 0) {
            for (OrderItem orderItem : orderItems) {
                Integer productId = orderItem.getProductId();
                Integer quantiy = orderItem.getQuantity();
                Product product = productMapper.selectByPrimaryKey(productId);
                product.setStock(product.getStock() - quantiy);
                productMapper.updateByPrimaryKey(product);
            }
        }
    }

    /**
     * 计算订单总价格
     */
    private BigDecimal sumMoney(List<OrderItem> orderItems) {
        BigDecimal bigDecimal = new BigDecimal("0");
        for (OrderItem orderItem : orderItems) {
            bigDecimal = BigdecimalUtils.add(bigDecimal.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return bigDecimal;
    }

    /**
     * 创建订单
     *
     * @param userId
     * @return
     */
    private Order createOrder(Integer userId, Integer shippingId, BigDecimal orderTotalprice) {
        Order order = new Order();
        order.setOrderNo(OrderIdUtils.generateUniqueKey());
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setStatus(Const.OrderStaEnum.ORDER_UN_PAY.getCode());
        order.setPayment(orderTotalprice);
        order.setPostage(0);
        order.setPaymentType(Const.PayType.ON_LINE.getCode());
        /*保存订单 */
        int result = orderMapper.insert(order);
        if (result > 0) {
            return order;
        }
        return null;
    }

    /**
     * 详情
     *
     * @param userId
     * @param cartList
     * @return
     */
    private ServerResponse getCartOrderItem(Integer userId, List<Cart> cartList) {
        if (cartList == null || cartList.size() <= 0) {
            return ServerResponse.createServerResponseByError("购物车为空");
        }
        List<OrderItem> orderItemList = Lists.newArrayList();
        for (Cart cart : cartList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setUserId(userId);
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if (product == null) {
                return ServerResponse.createServerResponseByError("商品不存在");
            }
            /* 商品下架或删除 */
            if (product.getStatus() != ProductStatusEnum.UP.getCode()) {
                return ServerResponse.createServerResponseByError(product.getName() + "未上架");
            }
            /* 判断库存 */
            if (product.getStock() < cart.getQuantity()) {
                return ServerResponse.createServerResponseByError("库存不足");
            }
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setTotalPrice(BigdecimalUtils.mul(product.getPrice().doubleValue(), cart.getQuantity().doubleValue()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createServerResponseBySuccess(orderItemList);
    }

    /**
     * 支付
     *
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse pay(Integer userId, Long orderNo) {
        if (orderNo == null) {
            return ServerResponse.createServerResponseByError("订单号不能为空");
        }
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createServerResponseByError("订单不存在");
        }
        List<OrderItem> orderItem = orderItemMapper.selectByOrderNo(orderNo);
        String productName = "";
        for (OrderItem item : orderItem) {
            productName = item.getProductName();
        }
        /* 调用支付宝接口 */
        return test_trade_precreate(order, productName);
    }

    /**
     * 支付宝回调接口
     *
     * @param map
     * @return
     */
    @Override
    public ServerResponse ailpay_callback(Map<String, String> map) {
        //step1 获取订单号
        Long orderNo = Long.parseLong(map.get("out_trade_no"));
        //step2 获取流水号
        String trade_no = map.get("trade_no");
        //step3 支付状态
        String trade_status = map.get("trade_status");
        //step4 支付时间
        String payment_time = map.get("gmt_payment");
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createServerResponseByError(orderNo + "订单错误");
        }
        /* 支付状态 */
        if (order.getStatus() >= Const.OrderStaEnum.ORDER_PAYED.getCode()) {
            /* 防止支付宝重复回调 */
            return ServerResponse.createServerResponseByError("支付宝重复调用");
        }
        if (trade_status.equals(Const.TRADE_SUCCESS)) {
            /* 支付成功 */
            /* 更改订单状态，更改支付时间 */
            order.setStatus(Const.OrderStaEnum.ORDER_PAYED.getCode());
            order.setSendTime(DateUtils.strToDate(payment_time));
            orderMapper.updateByPrimaryKey(order);
        }
        //保存支付信息
        PayInfo payInfo = new PayInfo();
        payInfo.setOrderNo(orderNo);
        payInfo.setPayPlatform(Const.PayPlatType.ALIPAY.getCode());
        payInfo.setPlatfromStatus(trade_status);
        payInfo.setPlatfromNumber(trade_no);
        payInfo.setUserId(order.getUserId());
        int result = payInfoMapper.insert(payInfo);
        if (result > 0) {
            return ServerResponse.createServerResponseBySuccess();
        }
        return ServerResponse.createServerResponseByError("支付信息保存失败");
    }

    /**
     * 订单支付状态查询
     *
     * @return
     */
    @Override
    public ServerResponse query_order_pay_stratus(Long orderNo) {
        if (orderNo == null) {
            return ServerResponse.createServerResponseByError("订单号不能为空");
        }
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createServerResponseByError("订单不存在");
        }
        if (order.getStatus() == Const.OrderStaEnum.ORDER_PAYED.getCode()) {
            return ServerResponse.createServerResponseBySuccess(true);
        }
        return ServerResponse.createServerResponseByError("false");
    }

    /**
     * 定时关闭订单
     *
     * @param time
     */
    @Override
    public void colseOrder(String time) {
        /* 未付款，超时 */
        List<Order> orders = orderMapper.selectBycreateTime(Const.OrderStaEnum.ORDER_UN_PAY.getCode(), time);
        if (orders != null && orders.size() > 0) {
            for (Order order : orders) {
                List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
                if (orderItemList != null && orderItemList.size() > 0) {
                    for (OrderItem orderItem : orderItemList) {
                        /* 加锁，库存更新后才能访问 */
                        Integer stock = productMapper.getProductstock(orderItem.getProductId());

                        if (stock == null) {
                            continue;
                        }
                        /* 返还库存 */
                        stock = stock + orderItem.getQuantity();
                        Product product = new Product();
                        product.setId(orderItem.getProductId());
                        //更新商品库存
                        product.setStock(stock);
                        productMapper.updateProductKeySelective(product);
                    }
                }
                /* 关闭订单 */
                order.setStatus(Const.OrderStaEnum.ORDER_CANCELED.getCode());
                order.setCloseTime(new Date());
                orderMapper.updateByPrimaryKey(order);
            }

        }
    }

    //////////////////////////////支付相关////////////////////////////////////////////
    private static Log log = LogFactory.getLog(Main.class);

    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;

    // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
    private static AlipayTradeService tradeWithHBService;

    // 支付宝交易保障接口服务，供测试接口api使用，请先阅读readme.txt
    private static AlipayMonitorService monitorService;

    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

        /** 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数 否则使用代码中的默认设置 */
        monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
                .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK")
                .setFormat("json").build();
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

    /*public static void main(String[] args) {
        Main main = new Main();

        // 系统商商测试交易保障接口api
        //        main.test_monitor_sys();

        // POS厂商测试交易保障接口api
        //        main.test_monitor_pos();

        // 测试交易保障接口调度
        //        main.test_monitor_schedule_logic();

        // 测试当面付2.0支付（使用未集成交易保障接口的当面付2.0服务）
        //        main.test_trade_pay(tradeService);

        // 测试查询当面付2.0交易
        //        main.test_trade_query();

        // 测试当面付2.0退货
        //        main.test_trade_refund();

        // 测试当面付2.0生成支付二维码
        main.test_trade_precreate();
    }
*/
    // 测试系统商交易保障调度
    public void test_monitor_schedule_logic() {
        // 启动交易保障线程
        DemoHbRunner demoRunner = new DemoHbRunner(monitorService);
        demoRunner.setDelay(5); // 设置启动后延迟5秒开始调度，不设置则默认3秒
        demoRunner.setDuration(10); // 设置间隔10秒进行调度，不设置则默认15 * 60秒
        demoRunner.schedule();

        // 启动当面付，此处每隔5秒调用一次支付接口，并且当随机数为0时交易保障线程退出
        while (Math.random() != 0) {
            test_trade_pay(tradeWithHBService);
            Utils.sleep(5 * 1000);
        }

        // 满足退出条件后可以调用shutdown优雅安全退出
        demoRunner.shutdown();
    }

    // 系统商的调用样例，填写了所有系统商商需要填写的字段
    public void test_monitor_sys() {
        // 系统商使用的交易信息格式，json字符串类型
        List<SysTradeInfo> sysTradeInfoList = new ArrayList<SysTradeInfo>();
        sysTradeInfoList.add(SysTradeInfo.newInstance("00000001", 5.2, HbStatus.S));
        sysTradeInfoList.add(SysTradeInfo.newInstance("00000002", 4.4, HbStatus.F));
        sysTradeInfoList.add(SysTradeInfo.newInstance("00000003", 11.3, HbStatus.P));
        sysTradeInfoList.add(SysTradeInfo.newInstance("00000004", 3.2, HbStatus.X));
        sysTradeInfoList.add(SysTradeInfo.newInstance("00000005", 4.1, HbStatus.X));

        // 填写异常信息，如果有的话
        List<ExceptionInfo> exceptionInfoList = new ArrayList<ExceptionInfo>();
        exceptionInfoList.add(ExceptionInfo.HE_SCANER);
        //        exceptionInfoList.add(ExceptionInfo.HE_PRINTER);
        //        exceptionInfoList.add(ExceptionInfo.HE_OTHER);

        // 填写扩展参数，如果有的话
        Map<String, Object> extendInfo = new HashMap<String, Object>();
        //        extendInfo.put("SHOP_ID", "BJ_ZZ_001");
        //        extendInfo.put("TERMINAL_ID", "1234");

        String appAuthToken = "应用授权令牌";//根据真实值填写

        AlipayHeartbeatSynRequestBuilder builder = new AlipayHeartbeatSynRequestBuilder()
                .setAppAuthToken(appAuthToken).setProduct(com.alipay.demo.trade.model.hb.Product.FP).setType(Type.CR)
                .setEquipmentId("cr1000001").setEquipmentStatus(EquipStatus.NORMAL)
                .setTime(Utils.toDate(new Date())).setStoreId("store10001").setMac("0a:00:27:00:00:00")
                .setNetworkType("LAN").setProviderId("2088911212323549") // 设置系统商pid
                .setSysTradeInfoList(sysTradeInfoList) // 系统商同步trade_info信息
                //                .setExceptionInfoList(exceptionInfoList)  // 填写异常信息，如果有的话
                .setExtendInfo(extendInfo) // 填写扩展信息，如果有的话
                ;

        MonitorHeartbeatSynResponse response = monitorService.heartbeatSyn(builder);
        dumpResponse(response);
    }

    // POS厂商的调用样例，填写了所有pos厂商需要填写的字段
    public void test_monitor_pos() {
        // POS厂商使用的交易信息格式，字符串类型
        List<PosTradeInfo> posTradeInfoList = new ArrayList<PosTradeInfo>();
        posTradeInfoList.add(PosTradeInfo.newInstance(HbStatus.S, "1324", 7));
        posTradeInfoList.add(PosTradeInfo.newInstance(HbStatus.X, "1326", 15));
        posTradeInfoList.add(PosTradeInfo.newInstance(HbStatus.S, "1401", 8));
        posTradeInfoList.add(PosTradeInfo.newInstance(HbStatus.F, "1405", 3));

        // 填写异常信息，如果有的话
        List<ExceptionInfo> exceptionInfoList = new ArrayList<ExceptionInfo>();
        exceptionInfoList.add(ExceptionInfo.HE_PRINTER);

        // 填写扩展参数，如果有的话
        Map<String, Object> extendInfo = new HashMap<String, Object>();
        //        extendInfo.put("SHOP_ID", "BJ_ZZ_001");
        //        extendInfo.put("TERMINAL_ID", "1234");

        AlipayHeartbeatSynRequestBuilder builder = new AlipayHeartbeatSynRequestBuilder()
                .setProduct(com.alipay.demo.trade.model.hb.Product.FP)
                .setType(Type.SOFT_POS)
                .setEquipmentId("soft100001")
                .setEquipmentStatus(EquipStatus.NORMAL)
                .setTime("2015-09-28 11:14:49")
                .setManufacturerPid("2088000000000009")
                // 填写机具商的支付宝pid
                .setStoreId("store200001").setEquipmentPosition("31.2433190000,121.5090750000")
                .setBbsPosition("2869719733-065|2896507033-091").setNetworkStatus("gggbbbgggnnn")
                .setNetworkType("3G").setBattery("98").setWifiMac("0a:00:27:00:00:00")
                .setWifiName("test_wifi_name").setIp("192.168.1.188")
                .setPosTradeInfoList(posTradeInfoList) // POS厂商同步trade_info信息
                //                .setExceptionInfoList(exceptionInfoList) // 填写异常信息，如果有的话
                .setExtendInfo(extendInfo) // 填写扩展信息，如果有的话
                ;

        MonitorHeartbeatSynResponse response = monitorService.heartbeatSyn(builder);
        dumpResponse(response);
    }

    // 测试当面付2.0支付
    public void test_trade_pay(AlipayTradeService service) {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = "tradepay" + System.currentTimeMillis()
                + (long) (Math.random() * 10000000L);

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店消费”
        String subject = "xxx品牌xxx门店当面付消费";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = "0.01";

        // (必填) 付款条码，用户支付宝钱包手机app点击“付款”产生的付款条码
        String authCode = "用户自己的支付宝付款码"; // 条码示例，286648048691290423
        // (可选，根据需要决定是否使用) 订单可打折金额，可以配合商家平台配置折扣活动，如果订单部分商品参与打折，可以将部分商品总价填写至此字段，默认全部商品可打折
        // 如果该值未传入,但传入了【订单总金额】,【不可打折金额】 则该值默认为【订单总金额】- 【不可打折金额】
        //        String discountableAmount = "1.00"; //

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0.0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品3件共20.00元"
        String body = "购买商品3件共20.00元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        String providerId = "2088100200300400500";
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(providerId);

        // 支付超时，线下扫码交易定义为5分钟
        String timeoutExpress = "5m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx面包", 1000, 1);
        // 创建好一个商品后添加至商品明细列表
        goodsDetailList.add(goods1);

        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
        goodsDetailList.add(goods2);

        String appAuthToken = "应用授权令牌";//根据真实值填写

        // 创建条码支付请求builder，设置请求参数
        AlipayTradePayRequestBuilder builder = new AlipayTradePayRequestBuilder()
                //            .setAppAuthToken(appAuthToken)
                .setOutTradeNo(outTradeNo).setSubject(subject).setAuthCode(authCode)
                .setTotalAmount(totalAmount).setStoreId(storeId)
                .setUndiscountableAmount(undiscountableAmount).setBody(body).setOperatorId(operatorId)
                .setExtendParams(extendParams).setSellerId(sellerId)
                .setGoodsDetailList(goodsDetailList).setTimeoutExpress(timeoutExpress);

        // 调用tradePay方法获取当面付应答
        AlipayF2FPayResult result = service.tradePay(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝支付成功: )");
                break;

            case FAILED:
                log.error("支付宝支付失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，订单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
    }

    // 测试当面付2.0查询订单
    public void test_trade_query() {
        // (必填) 商户订单号，通过此商户订单号查询当面付的交易状态
        String outTradeNo = "tradepay14817938139942440181";

        // 创建查询请求builder，设置请求参数
        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder()
                .setOutTradeNo(outTradeNo);

        AlipayF2FQueryResult result = tradeService.queryTradeResult(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("查询返回该订单支付成功: )");

                AlipayTradeQueryResponse response = result.getResponse();
                dumpResponse(response);

                log.info(response.getTradeStatus());
                if (Utils.isListNotEmpty(response.getFundBillList())) {
                    for (TradeFundBill bill : response.getFundBillList()) {
                        log.info(bill.getFundChannel() + ":" + bill.getAmount());
                    }
                }
                break;

            case FAILED:
                log.error("查询返回该订单支付失败或被关闭!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，订单支付状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
    }

    // 测试当面付2.0退款
    public void test_trade_refund() {
        // (必填) 外部订单号，需要退款交易的商户外部订单号
        String outTradeNo = "tradepay14817938139942440181";

        // (必填) 退款金额，该金额必须小于等于订单的支付金额，单位为元
        String refundAmount = "0.01";

        // (可选，需要支持重复退货时必填) 商户退款请求号，相同支付宝交易号下的不同退款请求号对应同一笔交易的不同退款申请，
        // 对于相同支付宝交易号下多笔相同商户退款请求号的退款交易，支付宝只会进行一次退款
        String outRequestNo = "";

        // (必填) 退款原因，可以说明用户退款原因，方便为商家后台提供统计
        String refundReason = "正常退款，用户买多了";

        // (必填) 商户门店编号，退款情况下可以为商家后台提供退款权限判定和统计等作用，详询支付宝技术支持
        String storeId = "test_store_id";

        // 创建退款请求builder，设置请求参数
        AlipayTradeRefundRequestBuilder builder = new AlipayTradeRefundRequestBuilder()
                .setOutTradeNo(outTradeNo).setRefundAmount(refundAmount).setRefundReason(refundReason)
                .setOutRequestNo(outRequestNo).setStoreId(storeId);

        AlipayF2FRefundResult result = tradeService.tradeRefund(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝退款成功: )");
                break;

            case FAILED:
                log.error("支付宝退款失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，订单退款状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
    }

    // 测试当面付2.0生成支付二维码
    public ServerResponse test_trade_precreate(Order order, String productName) {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
       /* String outTradeNo = "tradeprecreate" + System.currentTimeMillis()
                + (long) (Math.random() * 10000000L);*/
        String outTradeNo = String.valueOf(order.getOrderNo());

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = productName + "订单" + order.getOrderNo() + "当面付扫码消费" + order.getPayment().intValue();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = String.valueOf(order.getPayment().doubleValue());

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "购买商品3件共" + order.getPayment() + "元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
        if (orderItemList != null || orderItemList.size() > 0) {
            for (OrderItem orderItem : orderItemList) {
                GoodsDetail goodsDetail = GoodsDetail.newInstance(String.valueOf(orderItem.getProductId()), orderItem.getProductName(), orderItem.getCurrentUnitPrice().longValue(), orderItem.getQuantity());
                goodsDetailList.add(goodsDetail);
            }
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                //支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setNotifyUrl("http://vj2pws.natappfree.cc/learnshopping/portal/order/alipay_callback.do")
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                // 需要修改为运行机器上的路径
                String filePath = String.format("D:/files/qr-%s.png",
                        response.getOutTradeNo());
                log.info("filePath:" + filePath);
                /* 图片保存位置 */
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                File file = new File(filePath);
                /*FTPUtil.uplocadFile(Lists.newArrayList(file));*/
                Map map = Maps.newHashMap();
                map.put("orderNo", order.getOrderNo());
                map.put("qrCood", filePath);
                return ServerResponse.createServerResponseBySuccess(map);
            case FAILED:
                log.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        return ServerResponse.createServerResponseByError("下单失败!!!");
    }
}

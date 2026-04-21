package com.qf.aspect;

import com.qf.common.utils.LogUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class UserBehaviorAspect {
    
    // 定义切入点：拦截所有控制器方法
    @Pointcut("execution(* com.qf.controller.*.*(..))")
    public void controllerPointcut() {}
    
    // 定义商品相关的切入点
    @Pointcut("execution(* com.qf.controller.GoodsController.*(..)) || execution(* com.qf.controller.GoodsDetailController.*(..))")
    public void goodsPointcut() {}
    
    // 定义购物车相关的切入点
    @Pointcut("execution(* com.qf.controller.CartController.*(..))")
    public void cartPointcut() {}
    
    // 定义订单相关的切入点
    @Pointcut("execution(* com.qf.controller.OrderController.*(..))")
    public void orderPointcut() {}
    
    // 商品浏览行为拦截
    @AfterReturning("goodsPointcut() && args(.., request)")
    public void logGoodsView(JoinPoint joinPoint, HttpServletRequest request) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        if (methodName.equals("getGoodsById")) {
            // 解析商品ID
            for (Object arg : args) {
                if (arg instanceof Integer) {
                    int goodsId = (Integer) arg;
                    LogUtils.logUserView(request, goodsId, "Goods ID: " + goodsId);
                    break;
                }
            }
        }
    }
    
    // 购物车行为拦截
    @AfterReturning("cartPointcut() && args(.., request)")
    public void logCartAction(JoinPoint joinPoint, HttpServletRequest request) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        if (methodName.equals("addCart")) {
            // 解析商品ID和数量
            int goodsId = 0;
            int quantity = 1;
            String goodsName = "Unknown";
            
            for (Object arg : args) {
                if (arg instanceof Integer && goodsId == 0) {
                    goodsId = (Integer) arg;
                } else if (arg instanceof Integer && goodsId != 0) {
                    quantity = (Integer) arg;
                } else if (arg instanceof String) {
                    goodsName = (String) arg;
                }
            }
            
            if (goodsId > 0) {
                LogUtils.logAddToCart(request, goodsId, goodsName, quantity);
            }
        } else if (methodName.equals("removeCart")) {
            // 解析商品ID
            for (Object arg : args) {
                if (arg instanceof Integer) {
                    int goodsId = (Integer) arg;
                    LogUtils.logRemoveFromCart(request, goodsId, "Goods ID: " + goodsId);
                    break;
                }
            }
        }
    }
    
    // 订单行为拦截
    @AfterReturning("orderPointcut() && args(.., request)")
    public void logOrderAction(JoinPoint joinPoint, HttpServletRequest request) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        if (methodName.equals("submitOrder")) {
            // 解析订单相关参数
            int goodsId = 0;
            String goodsName = "Unknown";
            String orderId = "Unknown";
            
            for (Object arg : args) {
                if (arg instanceof Integer && goodsId == 0) {
                    goodsId = (Integer) arg;
                } else if (arg instanceof String && goodsName.equals("Unknown")) {
                    goodsName = (String) arg;
                } else if (arg instanceof String && orderId.equals("Unknown")) {
                    orderId = (String) arg;
                }
            }
            
            if (goodsId > 0) {
                LogUtils.logUserOrder(request, goodsId, goodsName, orderId);
            }
        } else if (methodName.equals("payOrder")) {
            // 解析支付相关参数
            int goodsId = 0;
            String goodsName = "Unknown";
            String orderId = "Unknown";
            double amount = 0.0;
            
            for (Object arg : args) {
                if (arg instanceof Integer && goodsId == 0) {
                    goodsId = (Integer) arg;
                } else if (arg instanceof String && goodsName.equals("Unknown")) {
                    goodsName = (String) arg;
                } else if (arg instanceof String && orderId.equals("Unknown")) {
                    orderId = (String) arg;
                } else if (arg instanceof Double) {
                    amount = (Double) arg;
                }
            }
            
            if (goodsId > 0) {
                LogUtils.logUserPayment(request, goodsId, goodsName, orderId, amount);
            }
        }
    }
    
    // 通用行为拦截
    @AfterReturning("controllerPointcut()")
    public void logGeneralBehavior(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getName();
        
        // 记录一般访问行为
        System.out.println("User accessed: " + className + "." + methodName);
    }
}
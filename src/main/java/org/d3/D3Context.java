package org.d3;

import io.netty.channel.EventLoopGroup;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.d3.util.Config;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

@Component
public class D3Context implements Context, LifeCycle, ApplicationContextAware{

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		D3Context.applicationContext = applicationContext;
	}

	private static ApplicationContext applicationContext;
	
	public static Object getBean(String beanName){
		if (null == beanName){
			return null;
		}
		return applicationContext.getBean(beanName);
	}
	
	private Map<String, Game> games = Maps.newHashMap();
	
	private void initGames(){
		System.out.println(Config.getInstance().getItem("game/val"));
	}
	
	public Game getGame(String name){
		return null;
	}
	
	@PostConstruct
	public void start() {
		initGames();
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}
//	
//	public EventLoopGroup getEventLoopGroup(){
//		
//	}
}
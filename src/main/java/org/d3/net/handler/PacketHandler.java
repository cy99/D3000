package org.d3.net.handler;

import org.d3.core.mybatis.domain.User;
import org.d3.module.Dispatcher;
import org.d3.module.Module;
import org.d3.net.packet.InPacket;
import org.d3.net.packet.Protobufs;
import org.d3.net.session.Session;
import org.d3.net.session.SessionManager;
import org.d3.net.session.Sessions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Component
@Scope("prototype")
public class PacketHandler extends SimpleChannelInboundHandler<InPacket> {
	
	private static Logger LOG = LoggerFactory.getLogger(PacketHandler.class);
	private Session session;
	
	@Autowired
	private Dispatcher dispatcher;
	
	public PacketHandler(){}

	/**
	 * 为什么调用不到?
	 * 因为active的时候,这个handler还没进入pipeline
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//		session = Sessions.newSession(ctx.channel());
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, InPacket ask)
			throws Exception {
//		charactor.onMessage(msg);
		if(ask.getModule() == Module.LOGIN){
			byte[] data = (byte[]) ask.getTuple();
			User user = Protobufs.getLoginUser(data);
			Session session = null;// = SessionManager.instance().getByName(user.getName());
			if(session != null){
				if(session.isActive()){
					LOG.error("repeat login! " + user.getName() + " is online now!");
					ctx.close();
					return;
				}
				else{
					session.setChannel(ctx.channel());
				}
			}
			else{
				session = Sessions.newSession(ctx.channel());
			}
			this.session = session;
			ask.setTuple(user);
		}
		if(LOG.isDebugEnabled()){
			LOG.debug(ask.toString());
		}
		dispatcher.dispatch(session, ask);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		ctx.close();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		System.out.println("inactive");
		if(session != null){
			session.close();
			SessionManager.instance().removeSession(session);
		}
//		PlayerSession playerSession = (PlayerSession) session;
//		if(charactor.getRoom() != null){
//			charactor.getRoom().leaveRoom(charactor);
//			if(charactor.getPlayer().isReady())
//				charactor.getRoom().playerUnPrepare();
//		}
//		playerSession.offLine();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		System.err.println(cause.getMessage());
	}
}

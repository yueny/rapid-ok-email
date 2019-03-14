package com.yueny.rapid.email.sender.entity;

import com.yueny.rapid.lang.util.time.SystemClock;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * email发送实体
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2018年2月28日 下午8:43:50
 *
 */
public class ThreadEmailEntry {
	/**
	 * 结束时间,毫秒
	 */
	private long endTime;
	/**
	 * 发送消息id
	 */
	@Getter
	@Setter
	private String msgId;
	/**
	 * 开始时间,毫秒
	 */
	private final long startTime;

	/**
	 * 创建一个新的entry。
	 */
	public ThreadEmailEntry() {
		this.startTime = SystemClock.now();
	}

	/**
	 * 取得entry持续的时间。
	 *
	 * @return entry持续的时间，如果entry还未结束，则返回<code>-1</code>
	 */
	public long getDuration() {
		if (endTime < startTime) {
			return -1;
		} else {
			return endTime - startTime;
		}
	}

	/**
	 * 判断当前entry是否结束。
	 *
	 * @return 如果entry已经结束，则返回<code>true</code>
	 */
	public boolean isReleased() {
		return endTime > 0;
	}

	/**
	 * 是否发送成功
	 */
	public boolean isSucess() {
		return StringUtils.isNotEmpty(msgId);
	}

	/**
	 * 结束当前entry，并记录结束时间。
	 */
	public void release() {
		endTime = SystemClock.now();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}

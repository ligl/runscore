package me.zohar.lottery.useraccount.domain;

import java.text.MessageFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import cn.hutool.core.util.NumberUtil;
import lombok.Getter;
import lombok.Setter;
import me.zohar.lottery.common.utils.IdUtils;
import me.zohar.lottery.constants.Constant;
import me.zohar.lottery.platform.domain.PlatformOrder;
import me.zohar.lottery.rechargewithdraw.domain.RechargeOrder;
import me.zohar.lottery.rechargewithdraw.domain.WithdrawRecord;

/**
 * 账变日志
 * 
 * @author zohar
 * @date 2019年1月17日
 *
 */
@Getter
@Setter
@Entity
@Table(name = "account_change_log", schema = "lottery")
@DynamicInsert(true)
@DynamicUpdate(true)
public class AccountChangeLog {

	/**
	 * 主键id
	 */
	@Id
	@Column(name = "id", length = 32)
	private String id;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 账变时间
	 */
	private Date accountChangeTime;

	/**
	 * 账变类型代码
	 */
	private String accountChangeTypeCode;

	/**
	 * 账变金额
	 */
	private Double accountChangeAmount;
	
	/**
	 * 保证金
	 */
	private Double cashDeposit;
	
	/**
	 * 备注
	 */
	private String note;

	/**
	 * 乐观锁版本号
	 */
	@Version
	private Long version;

	/**
	 * 用户账号id
	 */
	@Column(name = "user_account_id", length = 32)
	private String userAccountId;

	/**
	 * 用户账号
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private UserAccount userAccount;

	/**
	 * 构建充值账变日志
	 * 
	 * @param userAccount
	 * @param bettingOrder
	 * @return
	 */
	public static AccountChangeLog buildWithRecharge(UserAccount userAccount, RechargeOrder rechargeOrder) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(rechargeOrder.getOrderNo());
		log.setAccountChangeTime(rechargeOrder.getSettlementTime());
		log.setAccountChangeTypeCode(Constant.账变日志类型_账号充值);
		log.setAccountChangeAmount(NumberUtil.round(rechargeOrder.getActualPayAmount(), 4).doubleValue());
		log.setCashDeposit(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		return log;
	}

	/**
	 * 构建充值优惠账变日志
	 * 
	 * @param userAccount
	 * @param returnWater
	 * @param returnWaterRate
	 * @return
	 */
	public static AccountChangeLog buildWithRechargePreferential(UserAccount userAccount, double returnWater,
			Integer returnWaterRate) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(log.getId());
		log.setAccountChangeTime(new Date());
		log.setAccountChangeTypeCode(Constant.账变日志类型_充值优惠);
		log.setAccountChangeAmount(NumberUtil.round(returnWater, 4).doubleValue());
		log.setNote(MessageFormat.format("充值返水率:{0}%", 5));
		log.setCashDeposit(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		return log;
	}

	/**
	 * 构建确认已支付日志
	 * 
	 * @param userAccount
	 * @param platformOrder
	 * @return
	 */
	public static AccountChangeLog buildWithConfirmToPaid(UserAccount userAccount, PlatformOrder platformOrder) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(platformOrder.getOrderNo());
		log.setAccountChangeTime(platformOrder.getConfirmTime());
		log.setAccountChangeTypeCode(Constant.账变日志类型_确认支付扣款);
		log.setAccountChangeAmount(-platformOrder.getGatheringAmount());
		log.setCashDeposit(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		return log;
	}

	/**
	 * 构建发起提现账变日志
	 * 
	 * @param userAccount
	 * @param bettingOrder
	 * @return
	 */
	public static AccountChangeLog buildWithStartWithdraw(UserAccount userAccount, WithdrawRecord withdrawRecord) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(withdrawRecord.getOrderNo());
		log.setAccountChangeTime(withdrawRecord.getSubmitTime());
		log.setAccountChangeTypeCode(Constant.账变日志类型_账号提现);
		log.setAccountChangeAmount(NumberUtil.round(withdrawRecord.getWithdrawAmount(), 4).doubleValue());
		log.setCashDeposit(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		return log;
	}

}

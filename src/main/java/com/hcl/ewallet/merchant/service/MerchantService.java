package com.hcl.ewallet.merchant.service;

import com.hcl.ewallet.merchant.dto.WalletResponse;
import com.hcl.ewallet.merchant.entity.Merchant;
import com.hcl.ewallet.merchant.entity.MerchantTransaction;
import com.hcl.ewallet.merchant.entity.MerchantWallet;
import com.hcl.ewallet.merchant.entity.Settlement;
import com.hcl.ewallet.merchant.repository.MerchantRepository;
import com.hcl.ewallet.merchant.repository.MerchantTransactionRepository;
import com.hcl.ewallet.merchant.repository.MerchantWalletRepository;
import com.hcl.ewallet.merchant.repository.SettlementRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MerchantService {


    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private MerchantWalletRepository walletRepository;

    @Autowired
    private MerchantTransactionRepository transactionRepository;

    @Autowired
    private SettlementRepository settlementRepository;

    @Transactional
    public void creditMerchant(String merchantId, BigDecimal amount, String customerId) {

        BigDecimal fee = new BigDecimal("20");
        BigDecimal netAmount = amount.subtract(fee);

       // MerchantWallet wallet = walletRepository.findById("W1").orElseThrow();

        MerchantWallet wallet = walletRepository
                .findByMerchantId(merchantId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(netAmount));
        wallet.setUpdatedAt(LocalDateTime.now());

        walletRepository.save(wallet);

        String txnId = "TXN-" + System.currentTimeMillis();

        MerchantTransaction txn = new MerchantTransaction();
        txn.setMerchantId(merchantId);
        txn.setId(UUID.randomUUID().toString());
        txn.setTxnId(txnId);
        txn.setGrossAmount(amount);
        txn.setFeeDeducted(fee);
        txn.setNetAmount(netAmount);
        txn.setCustomerId(customerId);
        txn.setStatus("SUCCESS");
        txn.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(txn);

        Settlement settlement = new Settlement();
        settlement.setSettlementId(UUID.randomUUID().toString());
        settlement.setTxnId(txnId);
        settlement.setMerchantId(merchantId);
        settlement.setAmount(netAmount);
        settlement.setStatus("PENDING");
        settlement.setCreatedAt(LocalDateTime.now());

        settlementRepository.save(settlement);
    }


    @Transactional
    public String createMerchant(String name, String currency) {

        String merchantId = "M" + System.currentTimeMillis();
        String walletId = "W" + System.currentTimeMillis();

        Merchant merchant = new Merchant();
        merchant.setMerchantId(merchantId);
        merchant.setName(name);
        merchant.setStatus("ACTIVE");
        merchant.setCreatedAt(LocalDateTime.now());

        merchantRepository.save(merchant);

        MerchantWallet wallet = new MerchantWallet();
        wallet.setWalletId(walletId);
        wallet.setMerchantId(merchantId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency(currency);
        wallet.setUpdatedAt(LocalDateTime.now());

        walletRepository.save(wallet);

        return merchantId;
    }


    public WalletResponse getWalletByMerchantId(String merchantId) {

        MerchantWallet wallet = walletRepository
                .findByMerchantId(merchantId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for merchant " + merchantId));

        WalletResponse response = new WalletResponse();
        response.setWalletId(wallet.getWalletId());
        response.setMerchantId(wallet.getMerchantId());
        response.setBalance(wallet.getBalance());
        response.setCurrency(wallet.getCurrency());
        response.setUpdatedAt(wallet.getUpdatedAt());

        return response;
    }

    public List<MerchantTransaction> getTransactionsByMerchant(String merchantId) {

        return transactionRepository.findByMerchantId(merchantId);
    }


}

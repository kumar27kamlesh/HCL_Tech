package com.hcl.ewallet.wallet.service;

import com.hcl.ewallet.wallet.dto.TransactionRequest;
import com.hcl.ewallet.wallet.Wallet;
import com.hcl.ewallet.wallet.WalletEntry;
import com.hcl.ewallet.wallet.exception.InsufficientBalanceException;
import com.hcl.ewallet.wallet.WalletEntryRepository;
import com.hcl.ewallet.wallet.WalletRepository;

import jakarta.annotation.PostConstruct; // Import this
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong; // Thread-safe counter

@Service
public class WalletService {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);

    private final WalletRepository walletRepository;
    private final WalletEntryRepository walletEntryRepository;
    
    // 1️⃣ Thread-safe counter
    private final AtomicLong transactionCounter = new AtomicLong(0);

    public WalletService(WalletRepository walletRepository, WalletEntryRepository walletEntryRepository) {
        this.walletRepository = walletRepository;
        this.walletEntryRepository = walletEntryRepository;
    }

    // 2️⃣ Initialize counter from DB on startup
    @PostConstruct
    public void init() {
        Long maxId = walletEntryRepository.findMaxTransactionId();
        if (maxId != null) {
            transactionCounter.set(maxId);
            log.info("Initialized transaction counter to: {}", maxId);
        } else {
            transactionCounter.set(0);
            log.info("No previous transactions found. Counter starting at 0.");
        }
    }

    // 3️⃣ Helper to generate next ID (e.g., "TXN1", "TXN2")
    private String generateNextTransactionId() {
        return "TXN" + transactionCounter.incrementAndGet();
    }

    public Wallet createWallet(Long userId) {
        if(walletRepository.findByUserId(userId).isPresent()){
            throw new RuntimeException("Wallet already exists for user " + userId);
        }
        
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCreatedAt(LocalDateTime.now());
        
        return walletRepository.save(wallet);
    }

    public Wallet getWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + userId));
    }

    @Transactional
    public Wallet debit(Long userId, TransactionRequest request) {
        // 4️⃣ Generate ID automatically
        String txnId = generateNextTransactionId();
        
        Wallet wallet = getWallet(userId);

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient funds");
        }

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        Wallet savedWallet = walletRepository.save(wallet);

        // Pass generated ID
        saveEntry(savedWallet, request.getAmount(), txnId, WalletEntry.TransactionType.DEBIT);
        
        return savedWallet;
    }

    @Transactional
    public Wallet credit(Long userId, TransactionRequest request) {
        // 4️⃣ Generate ID automatically
        String txnId = generateNextTransactionId();

        Wallet wallet = getWallet(userId);
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        Wallet savedWallet = walletRepository.save(wallet);

        // Pass generated ID
        saveEntry(savedWallet, request.getAmount(), txnId, WalletEntry.TransactionType.CREDIT);
        
        return savedWallet;
    }

    // Updated helper method signature
    private void saveEntry(Wallet wallet, BigDecimal amount, String txnId, WalletEntry.TransactionType type) {
        WalletEntry entry = new WalletEntry();
        entry.setWallet(wallet);
        entry.setAmount(amount);
        entry.setType(type);
        entry.setTransactionId(txnId); // Set the generated ID
        entry.setBalanceAfter(wallet.getBalance());
        entry.setTimestamp(LocalDateTime.now());
        
        walletEntryRepository.save(entry);
    }
}
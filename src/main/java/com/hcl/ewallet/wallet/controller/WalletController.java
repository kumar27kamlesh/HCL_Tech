package com.hcl.ewallet.wallet.controller;

import com.hcl.ewallet.wallet.dto.TransactionRequest;
import com.hcl.ewallet.wallet.Wallet; // Ensure this imports your Entity correctly
import com.hcl.ewallet.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;
    
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }


    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestParam Long userId) {
        return ResponseEntity.ok(walletService.createWallet(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Wallet> getWallet(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getWallet(userId));
    }

    @PostMapping("/{userId}/debit")
    public ResponseEntity<Wallet> debit(@PathVariable Long userId, @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(walletService.debit(userId, request));
    }

    @PostMapping("/{userId}/credit")
    public ResponseEntity<Wallet> credit(@PathVariable Long userId, @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(walletService.credit(userId, request));
    }
}
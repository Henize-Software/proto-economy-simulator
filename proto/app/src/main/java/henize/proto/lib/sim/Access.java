/* Proto - Economy Simulator
Copyright (C) 2024 Joshua Henize

This program is free software: you can redistribute it and/or modify it under the terms of the
GNU General Public License as published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.
If not, see https://www.gnu.org/licenses/. */
package henize.proto.lib.sim;

import java.io.Serializable;

import henize.proto.lib.bank.Account;
import henize.proto.lib.bank.Bank;
import henize.proto.lib.directory.AuctionSystem;
import henize.proto.lib.directory.Directory;
import henize.proto.lib.stats.BankStats;
import henize.proto.lib.stats.ComStats;
import henize.proto.lib.stats.TraderStats;
import henize.proto.lib.traders.MasterTrader;

/**
 * Created by ACR411 on 12/13/2017.
 */

public class Access implements Serializable {
    public Simulator sim;
    public MasterTrader master;
    public BankStats bankStats;
    public ComStats comStats;
    public TraderStats traderStats;
    public Config config;
    public Account taxAccount;
    public Bank bank;
    public Directory dir;
    //public AuctionSystem auction;
}

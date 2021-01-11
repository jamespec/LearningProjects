#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Jan  7 22:05:04 2021

@author: James
"""

import smtplib

def send_email(message):
    """send_email - sends an email to the developers describing a bug."""

    # I special account as created to send the emails.
    # We login in the credentials for this account.
        
    sender_email = "williamschaefer@optonline.com"
    receiver_email = ["james315@icloud.com", "williamgeorgeschaefer@gmail.com"]
    
    # Open connection to the SMTP server in a with so that it is automatcally closed.
    with smtplib.SMTP("mail.optonline.net", 587) as s:
        # Switch to using TLS encryption
        s.starttls()
        
        # Send login info to the SMTP
        s.login("williamschaefer", "3472flatbush")
        
        message = """Subject: Baseball Bug Report\n\n""" + message
        
        # Send the message
        s.sendmail(sender_email, receiver_email, message)

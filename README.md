# Java Huffman Encoder

## What is it?

A lightweight desktop application for text compression and decompression using the Huffman Coding algorithm. Perfect for understanding how compression works or for compressing text files efficiently.

## What is Huffman Coding?

Huffman Coding is a lossless data compression algorithm that assigns variable-length codes to characters based on their frequency. Characters appearing more often get shorter codes, hence reducing overall file size. This makes it ideal for scenarios where certain characters dominate the text, resulting in significant space savings without any data loss.

## Features

- **Text Compression**: Compress text using Huffman encoding
- **Text Decompression**: Decompress Huffman-encoded data back to original text
- **Desktop GUI**: User-friendly interface with multiple specialized panels
- **File Operations**: Save and load compressed files with `.huff` extension
- **Compression Statistics**: View compression ratio and performance metrics in real-time
- **Clipboard Support**: Quickly copy/paste compressed content
- **Data Visualization**: See the Huffman tree structure and character frequency distribution
- **CSV Export**: Export the code table for further analysis

## Requirements

- **Java 21** or later

## How to use

- **Encoder Panel**: Paste or type your text to compress it using Huffman encoding
- **Decoder Panel**: Paste encoded data to decompress it back to the original text
- **Tree View**: Visualize the Huffman Node tree structure built from your data
- **Frequencies Panel**: See the frequency distribution of different characters in your input
- **Code Table**: Browse characters, their frequency, assigned codes, and bit contribution; export as CSV or copy the table
- **File I/O**: Open `.txt` files and encode them to `.huff` files, or decode existing `.huff` files back to text

Version 1.0 // 11.4.2026
